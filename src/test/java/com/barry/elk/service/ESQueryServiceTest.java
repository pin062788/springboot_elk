package com.barry.elk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.barry.elk.base.BaseTest;
import com.barry.elk.config.ESProperties;
import com.barry.elk.vo.business.VehicleInfo;
import org.apache.http.HttpHost;
import org.elasticsearch.Build;
import org.elasticsearch.Version;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SpanQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ES API接口测试
 *
 * @author: hants
 * @create: 2017/10/10 14:52
 * desc:
 */
public class ESQueryServiceTest extends BaseTest{

    private static final Logger logger = LoggerFactory.getLogger(ESQueryServiceTest.class);

    @Autowired
    private ESProperties esProperties;

    private static final String ip = "172.16.119.128";
    private static final Integer port = 9200;
    private static String index = "driver_service";
    private static String driverType = "vehicle_condition";
    private static String customerType = "customer";
    private static String idName = "";
    private VehicleInfo vehicleInfo = new VehicleInfo();
    private List<VehicleInfo> list = new ArrayList<>();

    /**
     * 初始化数据
     * */
    @Before
    public void prepare(){
        //单体数据
        vehicleInfo.setId("003");
        vehicleInfo.setSerialNo(3);
        vehicleInfo.setLevel("003");
        vehicleInfo.setGroupId("test-groupId"+"-"+"003");
        vehicleInfo.setCusId("test-cusId"+"-"+"003");
        vehicleInfo.setChangPai("test-changPai"+"-"+"003");
        vehicleInfo.setCheXi("test-cheXi"+"-"+"003");
        vehicleInfo.setCheXingDaiMa("test-cheXingDaiMa"+"-"+"003");
        vehicleInfo.setCheJiaHao("test-cheJiaHao"+"-"+"003");
        vehicleInfo.setFaDongJiHao("test-faDongJiHao"+"-"+"003");
        vehicleInfo.setChePaiHao("test-chePaiHao"+"-"+"003");
        vehicleInfo.setChePaiYanSe("test-chePaiYanSe"+"-"+"003");
        vehicleInfo.setBianSuXiangHao("test-bianSuXiangHao"+"-"+"003");
        vehicleInfo.setChanDi("test-chanDi"+"-"+"003");
        vehicleInfo.setGouCheDate(new Date());
        vehicleInfo.setBaoXianDate(new Date());
        vehicleInfo.setZhiZaoDate(new Date());
        vehicleInfo.setNeiShiYanSe("test-neiShiYanSe"+"-"+"003");
        vehicleInfo.setShangJianDanHao("test-shangJianDanHao"+"-"+"003");
        vehicleInfo.setHeGeZhengHao("test-heGeZhengHao"+"-"+"003");
        vehicleInfo.setJinKouZhengHao("test-jinKouZhengHao"+"-"+"003");
        vehicleInfo.setCheLiangGuiGe("test-cheLiangGuiGe"+"-"+"003");
        vehicleInfo.setCheLiangSort("test-cheLiangSort"+"-"+"003");
        vehicleInfo.setNextExaDate(new Date());
        vehicleInfo.setIsZiDianSale("test-isZiDianSale"+"-"+"003");
        vehicleInfo.setSaleName("test-saleName"+"-"+"003");
        vehicleInfo.setSaleBillNo("test-saleBillNo"+"-"+"003");
        //集合数据
        for (int i=1;i<11;i++){
            VehicleInfo vehicle = new VehicleInfo();
            vehicle.setId(String.valueOf(i));
            vehicle.setSerialNo(i);
            vehicle.setLevel(i+"");
            vehicle.setGroupId("test-groupId"+"-"+String.valueOf(i));
            vehicle.setCusId("test-cusId"+"-"+String.valueOf(i));
            vehicle.setChangPai("test-changPai"+"-"+String.valueOf(i));
            vehicle.setCheXi("test-cheXi"+"-"+String.valueOf(i));
            vehicle.setCheXingDaiMa("test-cheXingDaiMa"+"-"+String.valueOf(i));
            vehicle.setCheJiaHao("test-cheJiaHao"+"-"+String.valueOf(i));
            vehicle.setFaDongJiHao("test-faDongJiHao"+"-"+String.valueOf(i));
            vehicle.setChePaiHao("test-chePaiHao"+"-"+String.valueOf(i));
            vehicle.setChePaiYanSe("test-chePaiYanSe"+"-"+String.valueOf(i));
            vehicle.setBianSuXiangHao("test-bianSuXiangHao"+"-"+String.valueOf(i));
            vehicle.setChanDi("test-chanDi"+"-"+String.valueOf(i));
            vehicle.setGouCheDate(new Date());
            vehicle.setBaoXianDate(new Date());
            vehicle.setZhiZaoDate(new Date());
            vehicle.setNeiShiYanSe("test-neiShiYanSe"+"-"+String.valueOf(i));
            vehicle.setShangJianDanHao("test-shangJianDanHao"+"-"+String.valueOf(i));
            vehicle.setHeGeZhengHao("test-heGeZhengHao"+"-"+String.valueOf(i));
            vehicle.setJinKouZhengHao("test-jinKouZhengHao"+"-"+String.valueOf(i));
            vehicle.setCheLiangGuiGe("test-cheLiangGuiGe"+"-"+String.valueOf(i));
            vehicle.setCheLiangSort("test-cheLiangSort"+"-"+String.valueOf(i));
            vehicle.setNextExaDate(new Date());
            vehicle.setIsZiDianSale("test-isZiDianSale"+"-"+String.valueOf(i));
            vehicle.setSaleName("test-saleName"+"-"+String.valueOf(i));
            vehicle.setSaleBillNo("test-saleBillNo"+"-"+String.valueOf(i));
            list.add(vehicle);
        }
    }

    /**
     * ES High Level API Info API
     * */
    @Test
    public void testInfoApi(){
        try {
            RestClient lowLevelRestClient = RestClient.builder(new HttpHost(ip, port, "http")).build();
            RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);
            MainResponse response = client.info();
            logger.info("============>response:"+JSON.toJSONString(response));
            ClusterName clusterName = response.getClusterName();
            logger.info("============>clusterName:"+JSON.toJSONString(clusterName));
            String clusterUuid = response.getClusterUuid();
            logger.info("============>clusterUuid:"+clusterUuid);
            String nodeName = response.getNodeName();
            logger.info("============>nodeName:"+nodeName);
            Version version = response.getVersion();
            logger.info("============>version:"+JSON.toJSONString(version));
            Build build = response.getBuild();
            logger.info("============>build:"+JSON.toJSONString(build));
        } catch (IOException e) {
            logger.error("testInfoApi error {} ", e);
        }
    }

    /**
     * ES High Level API 单条数据新增
     * */
    @Test
    public void testSingleSave(){
        try {
            RestClient lowLevelRestClient = RestClient.builder(new HttpHost(ip, port, "http")).build();
            RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);

            IndexRequest request = new IndexRequest(index, driverType, "");
            //request.source("vehicle", JSON.toJSONString(vehicleInfo));
            request.source(JSON.toJSONString(vehicleInfo), XContentType.JSON);

        IndexResponse response = client.index(request);

        logger.info("======>IndexResponse:"+JSON.toJSONString(response));
    } catch (IOException e) {
        logger.error("testSave error {} ", e);
    }
    }

    /**
     * ES High Level API 单条数据删除
     * */
    @Test
    public void testSingleDelete(){
        try {
            RestClient lowLevelRestClient = RestClient.builder(new HttpHost(ip, port, "http")).build();
            RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);
            DeleteRequest request = new DeleteRequest(index, driverType, "AV8kQNQ4-FOCCCzR2T4Y");
            DeleteResponse deleteResponse = client.delete(request);
            logger.info("==============>testSingleDelete:"+JSON.toJSONString(deleteResponse));
        } catch (IOException e) {
            logger.error("testSingleDelete error {} ", e);
        }
    }

    /**
     * ES High Level API 批量新增数据
     * */
    @Test
    public void testBatchSave(){
        try {
            RestClient lowLevelRestClient = RestClient.builder(new HttpHost(ip, port, "http")).build();
            RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);

            BulkRequest bulkRequest = new BulkRequest();
            for (VehicleInfo vehicle : list){
                IndexRequest indexRequest = new IndexRequest(index, driverType, "").
                        source(JSON.toJSONString(vehicle), XContentType.JSON);
                bulkRequest.add(indexRequest);
            }
            BulkResponse bulkResponse = client.bulk(bulkRequest);
            logger.info("=============>bulkResponse:"+JSON.toJSONString(bulkResponse));
        } catch (IOException e) {
            logger.error("testBatchSave error {} ", e);
        }
    }

    /**
     * ES High Level API 不加查询条件,检索出所有数据
     * */
    @Test
    public void testSimpleQuery (){
        try {
            RestClient lowLevelRestClient = RestClient.builder(new HttpHost(ip, port, "http")).build();
            RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);

            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            searchSourceBuilder.from(0);
            searchSourceBuilder.size(4);//分页
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            //searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));//排序
            searchSourceBuilder.sort("serialNo", SortOrder.ASC);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest);
            logger.info("============>searchResponse:"+JSON.toJSONString(searchResponse));
            if (searchResponse!=null){
                SearchHits hits = searchResponse.getHits();
                if (hits!=null){
                    Iterator it = hits.iterator();
                    while (it.hasNext()){
                        SearchHit hit = (SearchHit) it.next();
                        //logger.info("======================>jsonstring:"+hit.getSourceAsString());
                        VehicleInfo info = JSON.parseObject(hit.getSourceAsString(), VehicleInfo.class);
                        if (info!=null){
                            logger.info("==================>id:"+info.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("testSelect error {} ", e);
        }
    }

    /**
     * ES High Level API 复杂查询,设置查询条件但不做分页查询
     * vehicle_condition.chanDi---QueryBuilders.matchQuery("chanDi", "test-chanDi-6")
     * */
    @Test
    public void testComplexQuery(){
        try {
            RestClient lowLevelRestClient = RestClient.builder(new HttpHost(ip, port, "http")).build();
            RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);

            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(driverType);
            searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchPhraseQuery("changPai", "test-changPai-6"));
            sourceBuilder.from(0);
            sourceBuilder.size(5);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest);
            logger.info("====testComplexQuery========>searchResponse:"+JSON.toJSONString(searchResponse));
            if (searchResponse!=null){
                SearchHits hits = searchResponse.getHits();
                if (hits!=null){
                    Iterator it = hits.iterator();
                    while (it.hasNext()){
                        SearchHit hit = (SearchHit) it.next();
                        VehicleInfo info = JSON.parseObject(hit.getSourceAsString(), VehicleInfo.class);
                        if (info!=null){
                            logger.info("==================>changPai:"+info.getChangPai());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("testSelect error {} ", e);
        }
    }

    @Test
    public void testGetProperties(){
        logger.info("===========>ClusterName:"+esProperties.getClusterName());
        logger.info("===========>NodeName:"+esProperties.getNodeName());
        logger.info("===========>ServerIp:"+esProperties.getServerIp());
        logger.info("===========>ServerPort:"+esProperties.getServerPort());
    }

}