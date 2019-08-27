/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.qlzf_hous_keeper.tools;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
@Component("resouse")
public class EsTools {

    /**
     * 单条信息索引，有ID,通过map传递。不传ID时ES将自动分配ID 有ID 存在map中以"_id"字段保存
     *
     * @param index_name
     * @param type_name
     * @param map
     * @return true or false
     */
    public static boolean index_doc(Client client , String index_name, String type_name, HashMap<String, Object> map) {
        //IndexRequestBuilder对象的常用操作：
        //1:setSource()添加文档源
        //2：setIndex,setType,setID  同样可以在prepareindex里带参设置
        //3：setRouting ,setParent 同GET方法一样
        //4：setOpType()参数有“index"，“create”两种，默认为index，当文档ID已经在索引之中时，文档被覆盖。
        //              当使用create参数时，索引操作将失败。
        //5:setVersion:设置版本号，若指定版本不存在，更新失败。改方法确保在读取和更新文当期间文档未被修改。
        IndexResponse response = null;
        try {
            if (null != map.get("_id")) {
                String ID = map.remove("_id").toString();
                response = client.prepareIndex(index_name, type_name, ID).setSource(map).get();
            } else {
                response = client.prepareIndex(index_name, type_name).setSource(map).get();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //IndexResponse 对象常用操作：
        //1:getIndex(),getTpye(),getId(),getVersion().
        //2：getMatches():返回匹配被索引文档的过滤器查询列表。
    }

    /**
     * 插入单条信息,值类型json格式<br>
     * The id is optional, if it is not provided, one will be generated
     * automatically
     *
     * @param index_name
     * @param type_name
     * @param jsonData
     * @param ID
     * @return
     */
    public IndexResponse index_doc(Client client , String index_name, String type_name, String jsonData, String ID) {
        IndexResponse response = null;
        try {
            // 创建索引库 需要注意的是.setRefresh(true)这里一定要设置,否则第一次建立索引查找不到数据
            if (ID.length() > 0) {
                response = client.prepareIndex(index_name, type_name, ID).setSource(jsonData).get();
            } else {
                response = client.prepareIndex(index_name, type_name).setSource(jsonData).get();
            }
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 根据ID单条检索数据
     *
     * @param index_name
     * @param type_name
     * @param ID
     */
    public static GetResponse getdoc_ByID(Client client , String index_name, String type_name, String ID) {
        //GetRequestBuilder实例可附加信息：
        //1:setIndex(string) setType(string) setId(string):添加位置信息
        //2:setRouting(string)设定路由值，确定将用哪个分片来执行请求
        //3:.setParent(string)设定文档的父文档ID。
        //4：setPreference(string)设置查询偏好。
        //5:setRefresh(boolean):是否需要在操作前执行刷新。默认false
        //6:setRealtime(boolean):指定get操作是否实时。默认ture。
        GetResponse response = null;
        try {
            response = client.prepareGet(index_name, type_name, ID)
                    .execute().actionGet();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //Get操作响应，GetResponse对象的常用方法：
        //1:isExists():文档存在？
        //2:getIndex()，getType(),getId(),getVersion();
        //3:isSourceEmpty()：文档源是否可用或者不包含在当前的返回文档中。
        //4：getSourceXXX()按指定格式读取文档。
        //5：getField(string)：返回便是文档字段的对象。
    }

    /**
     * 根据ID更新数据。(目前es更新操作只支持单条数据更新，因为更新是要检索+对比+合并+覆盖）
     *
     * @param index_name
     * @param type_name
     * @param ID
     * @param map
     * @return true or false
     */
    public static boolean update_byID(Client client , String index_name, String type_name, String ID, HashMap<String, Object> map) {
        try {
            //1:UpdateRequestBuilder对象可追加参数：
            //2:setScript(string):设置修改脚本。
            //3:setRetryOnConflict(int) 默认为0：发现读取版本与更新版本不一致时重新操作次数。
            //4：setDoc()设置跟新文档片段。如果设置了script，则文档将被忽略。
            //5:setUpsert() 如果文档不存在，它会被重新索引。如果存在，则更新。
            //6:setDocAsUpsert(boo):如果文档不存在，setdoc会将文档加入到索引中。默认false.
            UpdateResponse response = client.prepareUpdate(index_name, type_name, ID)
                    .setDocAsUpsert(true)
                    .setDoc(map)
                    .execute().actionGet();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID更新数据 数据类型<json> 字符串
     *
     * @param index_name
     * @param type_name
     * @param jsonData
     * @param ID
     * @return
     */
    public UpdateResponse update_byID(Client client , String index_name, String type_name, String jsonData, String ID) {
        try {
            UpdateResponse response = client.prepareUpdate(index_name, type_name, ID)
                    //.setDocAsUpsert(true)
                    .setDoc(jsonData)
                    .execute().actionGet();
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 根据ID 删除文档
     *
     * @param index_name
     * @param type_name
     * @param ID
     * @return DeleteResponse
     */
    public static DeleteResponse del_byID(Client client , String index_name, String type_name, String ID) {
        DeleteResponse response = null;
        try {
            //DeleteRequestBuilder实例可追加参数：
            //1：setVersion(long)指定删除的版本号。
            response = client.prepareDelete(index_name, type_name, ID)
                    .execute().actionGet();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 批量索引数据 有ID时以"_id”字段传值到map当中
     *
     * @param index_name
     * @param type_name
     * @param list
     * @return true or false
     */
    public  Boolean add_docs(Client client , String index_name, String type_name, List<Map> list) {
        Boolean boo = true;
        int count = 0;
        try {
            if (list.isEmpty()) {
              return false;
            } else {
                BulkRequestBuilder bulkRequest = client.prepareBulk();
                for (Map map : list) {
                    try {
                        Object ID = map.get("_id");
                        if (null == ID) {
                            bulkRequest.add(client.prepareIndex(index_name, type_name).setSource(map));
                        } else {
                            map.remove("_id");
                            bulkRequest.add(client.prepareIndex(index_name, type_name, ID.toString()).setSource(map));
                        }
                        count++;
                        //每一千条导一次，根据自己节点数，分片数决定。
                        if (count % 1000 == 0) {
                            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                            bulkRequest = client.prepareBulk();
                            if (bulkResponse.hasFailures()) {  //判断是否有导入失败的文档
                                boo = false;
                            }
                        } else if (count == list.size()) {
                            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
                            if (bulkResponse.hasFailures()) {  //判断是否有导入失败的文档
                                boo = false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return boo;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 执行搜索 返回top条数据
     *
     * @param queryBuilder 查询器
     * @param indexname 索引名称
     * @param type 索引类型
     * @param top 查询返回条数
     * @return
     */
    public SearchResponse searchInfo(Client client , QueryBuilder queryBuilder, String indexname, String type, int top) {
        try {
            SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder)
                    .setSize(top).execute().actionGet();
            return searchResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 执行搜索 返回第from条到from+pageSize的数据
     *
     * @param queryBuilder
     * @param indexname
     * @param type
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse searchInfo(Client client , QueryBuilder queryBuilder, String indexname, String type, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder).setFrom(from)
                    .setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 分页查询并排序带页数 (降序)
     *
     * @param queryBuilder
     * @param indexname
     * @param type
     * @param sortfield
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse searchInfo_Order_DESC(Client client , QueryBuilder queryBuilder, String indexname, String type, String sortfield, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder).setFrom(from)
                    .setSize(pageSize).addSort(sortfield, SortOrder.DESC).execute().actionGet();
            return searchResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 分页查询并排序带页数 (升序)
     *
     * @param queryBuilder
     * @param indexname
     * @param type
     * @param sortfield
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse searchInfo_Order_ASC(Client client , QueryBuilder queryBuilder, String indexname, String type, String sortfield, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder).setFrom(from)
                    .setSize(pageSize).addSort(sortfield, SortOrder.ASC).execute().actionGet();
            return searchResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 查询并指定返回的参数
     *
     * @param indexname
     * @param type
     * @param queryBuilder
     * @param fileds 返回的参数数组
     * * @param sortfield
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse searchInfoSetSourceFiled(Client client , String indexname, String type, QueryBuilder queryBuilder, String[] fileds, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(new String[]{type}).setQuery(queryBuilder).setFetchSource(fileds, null).setFrom(from).setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询并指定返回的参数,并排序(降序)
     *
     * @param indexname
     * @param type
     * @param queryBuilder
     * @param fileds 返回的参数数组
     * * @param sortfield 排序字段
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse searchInfoSetSourceFiled_DESC(Client client , String indexname, String type, QueryBuilder queryBuilder, String[] fileds, String sortfield, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(new String[]{type}).setQuery(queryBuilder).setFetchSource(fileds, null)
                    .addSort(sortfield, SortOrder.DESC).setFrom(from).setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询并指定返回的参数,并排序(升序)
     *
     * @param indexname
     * @param type
     * @param queryBuilder
     * @param fileds 返回的参数数组
     * * @param sortfield 排序字段
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse searchInfoSetSourceFiled_ASC(Client client , String indexname, String type, QueryBuilder queryBuilder, String[] fileds, String sortfield, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(new String[]{type}).setQuery(queryBuilder).setFetchSource(fileds, null)
                    .addSort(sortfield, SortOrder.ASC).setFrom(from).setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 多个id +排序+指定检索字段 (降序)
     *
     * @param index_name
     * @param type_name
     * @param fileds
     * @param sortfield
     * @param id_list
     * @return
     */
    public SearchResponse searchInfoByIDs_DESC(Client client , String index_name, String type_name, String[] fileds, String sortfield, List id_list) {
        SearchResponse searchResponse = null;
        try {
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            for (Object id : id_list) {
                QueryBuilder term = QueryBuilders.matchPhraseQuery("id", id);
                bqb.should(term);
            }
            searchResponse = client.prepareSearch(index_name).setTypes(new String[]{type_name}).setQuery(bqb).setFetchSource(fileds, null)
                    .addSort(sortfield, SortOrder.DESC).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 多个id +排序+指定检索字段 (升序)
     *
     * @param index_name
     * @param type_name
     * @param fileds
     * @param sortfield
     * @param id_list
     * @return
     */
    public SearchResponse searchInfoByIDs_ASC(Client client , String index_name, String type_name, String[] fileds, String sortfield, List id_list) {
        SearchResponse searchResponse = null;
        try {
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            for (Object id : id_list) {
                QueryBuilder term = QueryBuilders.matchPhraseQuery("id", id);
                bqb.should(term);
            }
            searchResponse = client.prepareSearch(index_name).setTypes(new String[]{type_name}).setQuery(bqb).setFetchSource(fileds, null)
                    .addSort(sortfield, SortOrder.ASC).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 多个id +指定检索字段
     *
     * @param index_name
     * @param type_name
     * @param fileds
     * @param sortfield
     * @param id_list
     * @return
     */
    public SearchResponse searchInfoByIDs(Client client , String index_name, String type_name, String[] fileds, String sortfield, List id_list) {
        SearchResponse searchResponse = null;
        try {
            BoolQueryBuilder bqb = QueryBuilders.boolQuery();
            for (Object id : id_list) {
                QueryBuilder term = QueryBuilders.matchPhraseQuery("id", id);
                bqb.should(term);
            }
            searchResponse = client.prepareSearch(index_name).setTypes(new String[]{type_name}).setQuery(bqb).setFetchSource(fileds, null)
                    .execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 地理位置排序并制定shouce中返回的字段。
     *
     * @param indexname
     * @param type
     * @param queryBuilder
     * @param fileds 返回的参数数组
     * @param sbd 用于排序的builder
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse geoDistanceSortSearchAndSetSourceFileds(Client client , String indexname, String type, QueryBuilder queryBuilder, String[] fileds, SortBuilder sbd, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(new String[]{type}).setQuery(queryBuilder).addSort(sbd).setFetchSource(fileds, null).setFrom(from).setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按照一定条件过滤数据并采用地理位置排序再指定shouce中返回的字段。
     *
     * @param indexname
     * @param type
     * @param queryBuilder
     * @param fileds
     * @param sbd
     * @param filterqb
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse geoDistanceSortSearchAndSetSourceFileds(Client client , String indexname, String type, QueryBuilder queryBuilder, String[] fileds, SortBuilder sbd, QueryBuilder filterqb, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(new String[]{type}).setPostFilter(filterqb).setQuery(queryBuilder).addSort(sbd).setFetchSource(fileds, null).setFrom(from).setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 地理位置排序，指定shouce中返回的字段,并聚合（分组）返回结果。
     *
     * @param indexname
     * @param type
     * @param queryBuilder
     * @param fileds
     * @param sbd
     * @param aggregation
     * @param from
     * @param pageSize
     * @return
     */
    public SearchResponse geoDistanceSortSearchAndSetSourceFileds(Client client , String indexname, String type, QueryBuilder queryBuilder, String[] fileds, SortBuilder sbd, AbstractAggregationBuilder aggregation, int from, int pageSize) {
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.prepareSearch(indexname).setTypes(new String[]{type}).setQuery(queryBuilder).addSort(sbd).addAggregation(aggregation).setFetchSource(fileds, null).setFrom(from).setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行查询并用时间进行过滤
     *
     * @param queryBuilder 查询器。
     * @param indexname 索引
     * @param pageSize 显示条数
     * @param type 字段
     * @param time yyyy-MM-dd时间格式
     * @return
     */
    public SearchResponse searchInfoByTime(Client client , QueryBuilder queryBuilder, String indexname, int pageSize, String type, Object time) {
        try {
//            AbstractAggregationBuilder aab ;
            QueryBuilder qbs = QueryBuilders.rangeQuery("pubtime").format("yyyy-MM-dd").gte(time);
            SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder).setPostFilter(qbs)
                    .setSize(pageSize).execute().actionGet();
            return searchResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 聚合查询 返回top条
     *
     * @param queryBuilder
     * @param indexname
     * @param type
     * @param top
     * @param aab
     * @return
     */
    public SearchResponse searchInfoAbstract(Client client , String indexname, String type, QueryBuilder queryBuilder, int top, AbstractAggregationBuilder aab) {
        try {
            SearchResponse searchResponse = client.prepareSearch(indexname).setTypes(type).setQuery(queryBuilder)
                    .setSize(top).addAggregation(aab).execute().actionGet();
            return searchResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 高亮 如需自定义返回字段和高亮效果 请重写方法
     *
     * @param client
     * @param index_name
     * @param type_name
     * @param filed 高亮字段名称
     * @param keyword 高亮关键字
     */
//    public  List<String> searchInfo_HighLight(Client client , String index_name, String type_name, String filed, String keyword) {
//        try {
//            SearchRequestBuilder searchRequestBuilder = client
//                    .prepareSearch(index_name).setTypes(type_name);
//            HighlightBuilder highlightBuilder = new HighlightBuilder().field(filed);
//            highlightBuilder.preTags("<span style=\"color:red\">");
//            highlightBuilder.postTags("</span>");
//            searchRequestBuilder.highlighter(highlightBuilder);
//            searchRequestBuilder.setQuery(QueryBuilders.matchQuery(filed, keyword));
//            SearchResponse response = searchRequestBuilder.execute().actionGet();
//            List<String> response_list = new ArrayList<>();
//            for (SearchHit hit : response.getHits().getHits()) {
////                System.out.print(hit.getId() + "   ");
////                System.out.println(hit.getSource().get(keyword));
//                //获取对应的高亮域
//                Map<String, HighlightField> result = hit.highlightFields();
//                //从设定的高亮域中取得指定域
//                HighlightField titleField = result.get(keyword);
//                //取得定义的高亮标签
//                Text[] titleTexts = titleField.fragments();
//                //为title串值增加自定义的高亮标签
//                String title = "";
//                for (Text text : titleTexts) {
//                    title += text;
//                }
//                response_list.add(title);
//            }
//            return response_list;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
