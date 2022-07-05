package com.qq.common.es.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.json.JsonData;
import com.qq.common.core.exception.ServiceException;
import com.qq.common.core.utils.StringUtils;
import com.qq.common.es.service.EsService;
import com.qq.common.es.vo.QueryVo;
import com.qq.common.es.vo.SearchCommonVO;
import com.qq.common.es.vo.SearchResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @Description: es操作实现类
 * @Author QinQiang
 * @Date 2022/4/26
 **/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EsServiceImpl implements EsService {

    final ElasticsearchClient esClient;

    @Override
    public boolean createIndex(String indexName) throws IOException {
        //创建索引并返回状态
        return esClient.indices().create(c -> c.index(indexName)).acknowledged();
    }

    @Override
    public GetIndexResponse getIndex(String indexName) throws IOException {
        return esClient.indices().get(c -> c.index(indexName));
    }

    @Override
    public DeleteIndexResponse deleteIndex(String indexName) throws IOException {
        return esClient.indices().delete(c -> c.index(indexName));
    }

    @Override
    public IndexResponse addDoc(String indexName, String id, Object document) throws IOException {
        IndexResponse indexResponse = esClient.index(IndexRequest.of(x -> {
            x.id(id);
            x.index(indexName);
            x.document(document);
            return x;
        }));
        return indexResponse;
    }

    @Override
    public UpdateResponse updateDoc(String indexName, String id, Object document) throws IOException {
        UpdateRequest<Object, Object> req = UpdateRequest.of(x -> x.index(indexName).id(id).doc(document));
        UpdateResponse<Object> update = esClient.update(req, Object.class);
        return update;
    }

    @Override
    public DeleteResponse deleteDoc(String indexName, String id) throws IOException {
        DeleteResponse deleteResponse = esClient.delete(c -> c.index(indexName).id(id));
        return deleteResponse;
    }

    @Override
    public <E> SearchResultVO<E> search(SearchCommonVO searchCommonVO, Class<E> clazz) {
        if (StringUtils.isEmpty(searchCommonVO.getIndexName())) {
            throw new ServiceException("索引不能为空!");
        }
        List<QueryVo> queryVos = searchCommonVO.getQueryVos();
        if (CollUtil.isEmpty(queryVos)) {
            throw new ServiceException("查询条件不能为空!");
        }
        // 处理查询条件
        List<Query> queries = processQuery(queryVos);
        try {
            SearchRequest req = SearchRequest.of(sr -> sr
                    .trackTotalHits(h -> h
                            .enabled(Boolean.TRUE)
                    )
                    .size(searchCommonVO.getRows())
                    .from(searchCommonVO.getRows() * (searchCommonVO.getPage() - 1))
                    .index(searchCommonVO.getIndexName())
                    .sort(s -> s.field(FieldSort.of(fs -> fs.field(searchCommonVO.getSortIndex()).order(SortOrder.valueOf(searchCommonVO.getSortOrder())))))
                    .query(q -> q.bool(b -> b.must(queries)))
                    .highlight(h -> {
                        if (searchCommonVO.isHighlight() && searchCommonVO.getHighlightField().length > 0) {
                            for (int idx = 0; idx < searchCommonVO.getHighlightField().length; ++idx) {
                                final String field = searchCommonVO.getHighlightField()[idx];
                                h.fields(field, HighlightField.of(hf -> hf
                                        .preTags("<font color='#e75213'>")
                                        .postTags("</font>"))).fragmentSize(1024);
                            }
                        } else {
                            h.fields(Collections.emptyMap()).fragmentSize(1024);
                        }
                        return h;
                    })
                    .aggregations(parseAggregation(searchCommonVO))
            );
            SearchResponse<E> searchResponse = esClient.search(req, clazz);
            return parseResponse(searchResponse);
        } catch (Exception e) {
            log.error("es查询出错", e);
        }
        return null;
    }

    /**
     * 处理查询条件
     *
     * @param queryVos
     */
    private List<Query> processQuery(List<QueryVo> queryVos) {
        List<Query> queries = new ArrayList<>();
        for (QueryVo queryVo : queryVos) {
            if ("0".equals(queryVo.getQueryType())) {
                Query query = MatchQuery.of(m -> m
                        .field(queryVo.getField())
                        .query(queryVo.getKeyword())
                )._toQuery();
                queries.add(query);
            } else if ("1".equals(queryVo.getQueryType())) {
                Query query = RangeQuery.of(r -> r
                        .field(queryVo.getField())
                        .gte(JsonData.of(queryVo.getGte()))
                        .lte(JsonData.of(queryVo.getLte()))
                )._toQuery();
                queries.add(query);
            }
        }
        return queries;
    }

    private Map<String, Aggregation> parseAggregation(SearchCommonVO searchCommonVO) {
        return new HashMap<>();
    }

    /**
     * 处理返回值
     *
     * @param res
     * @param <E>
     * @return
     */
    private <E> SearchResultVO<E> parseResponse(SearchResponse<E> res) {
        List<E> list = new ArrayList<>();
        res.hits().hits().forEach(h -> {
            E source = h.source();
            if (source != null) {
                for (String highlightKey : h.highlight().keySet()) {
                    StringBuilder highlightedVal = new StringBuilder();
                    h.highlight().get(highlightKey).forEach(highlightedVal::append);
                    // 设置属性值
                    BeanUtil.setFieldValue(source, highlightKey, highlightedVal);
                }
                list.add(source);
            }
        });

        SearchResultVO<E> searchResultVO = new SearchResultVO<>();
        searchResultVO.setList(list);
        searchResultVO.setTotal(res.hits().total().value());
        return searchResultVO;
    }
}
