package com.xyz.bu.es;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * Elasticsearch模板（api）
 *
 * @author xyz
 * @date 2020/10/12
 */
public class ElasticsearchTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchTemplate.class);

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 获取原始模板
     *
     * @return RestHighLevelClient
     */
    public RestHighLevelClient originalTemplate() {
        return restHighLevelClient;
    }

    /**
     * 索引是否存在
     *
     * @param index 索引名
     * @return true/false
     */
    public boolean indexExist(@NonNull String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        boolean result = false;
        try {
            result = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error("indexExist ex index={}", index, e);
        }

        return result;
    }

    /**
     * 索引是否存在
     *
     * @param request 请求体
     * @return true/false
     */
    public boolean indexExist(@NonNull GetIndexRequest request) {
        boolean result = false;
        try {
            result = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error("indexExist ex request={}", request.toString(), e);
        }

        return result;
    }

    /**
     * 创建索引
     *
     * @param index 索引名
     * @return true/false
     */
    public boolean createIndex(@NonNull String index) {
        if (indexExist(index)) {
            LOGGER.info(" index={} 已经存在", index);
            // 索引已存在，直接返回true
            return true;
        }

        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse resp = null;
        try {
            resp = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error("createIndex ex index={}", request.index(), e);
        }
        return !Objects.isNull(resp) && resp.isAcknowledged();
    }

    /**
     * 创建索引
     *
     * @param request 请求体
     * @return true/false
     */
    public boolean createIndex(@NonNull CreateIndexRequest request) {
        if (indexExist(request.index())) {
            LOGGER.info(" index={} 已经存在", request.index());
            // 索引已存在，直接返回true
            return true;
        }

        CreateIndexResponse resp = null;
        try {
            resp = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error("createIndex ex index={}", request.index(), e);
        }
        return !Objects.isNull(resp) && resp.isAcknowledged();
    }

    /**
     * 删除index
     *
     * @param index 索引名
     */
    public void deleteIndex(@NonNull String index) {
        if (!indexExist(index)) {
            LOGGER.info(" index={} 不存在", index);
            return;
        }
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.error("deleteIndex ex index={}", index, e);
        }
    }

    /**
     * 插入一条或更新一条（索引不存在时自动创建索引）
     *
     * @param index  索引名
     * @param entity 对象
     */
    public void insertOrUpdateOne(@NonNull String index, @NonNull ElasticsearchEntity entity) {
        IndexRequest request = new IndexRequest(index);
        if (StringUtils.isNotBlank(entity.getId())) {
            request.id(entity.getId());
        }
        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error("insertOrUpdateOne ex index={}", index, e);
        }
    }


    /**
     * 批量插入数据
     *
     * @param index 索引名
     * @param list  数据列表
     */
    public void insertBatch(@NonNull String index, @NonNull List<ElasticsearchEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(index).id(item.getId())
                .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.error("insertBatch ex index={}", index, e);
        }
    }

    /**
     * 根据id批量删除数据
     *
     * @param index  索引名
     * @param idList 待删除id列表
     */
    public <T> void deleteBatch(@NonNull String index, @NonNull Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(index, item.toString())));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.error("deleteBatch ex index={}", index, e);
        }
    }

    /**
     * @param index   索引名
     * @param builder 查询参数
     * @param c       结果类对象
     * @return java.util.List<T> 结果集
     */
    public <T> List<T> search(@NonNull String index, @NonNull SearchSourceBuilder builder, @NonNull Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            LOGGER.error("search ex index={}", index, e);
        }
        return Collections.emptyList();
    }


    /**
     * 根据查询的结果删除
     *
     * @param index   索引名
     * @param builder 查询参数
     */
    public void deleteByQuery(@NonNull String index, @NonNull QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(builder);

        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.error("deleteByQuery ex index={}", index, e);
        }
    }


}
