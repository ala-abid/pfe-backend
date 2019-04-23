package com.vermeg.ala17.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vermeg.ala17.document.AnswerDocument;
import com.vermeg.ala17.document.QuestionDocument;
import com.vermeg.ala17.entity.Answer;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ESQuestionService {
    private static final String INDEX = "question";
    private static final String TYPE = "_doc";
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;

    @Autowired
    public ESQuestionService(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public List<QuestionDocument> findByQuestion(String query, String field) throws Exception {
        SearchRequest searchRequest = buildSearchRequest(INDEX, TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhrasePrefixQuery(field, query).slop(5));
        //searchSourceBuilder = addHighlighter(searchSourceBuilder, "title");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResultQuestion(response);
    }

    public List<AnswerDocument> findByAnswer(String txt) throws Exception {
        SearchRequest searchRequest = buildSearchRequest("answer", TYPE);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhrasePrefixQuery("txt", txt).slop(5));
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResultAnswer(response);
    }

    private SearchRequest buildSearchRequest(String index, String type) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(type);
        return searchRequest;
    }

    private List<QuestionDocument> getSearchResultQuestion(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        //QuestionDocument questionDocument = null;
        List<QuestionDocument> questionDocumentList = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            questionDocumentList.add(convertMapToQuestionDoc(hit.getSourceAsMap()));
        }
        //getHighlights(response, "technologies.name");
        return questionDocumentList;
    }

    private List<AnswerDocument> getSearchResultAnswer (SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        List<AnswerDocument> answerDocuments = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            answerDocuments.add(convertMapToAnswerDoc(hit.getSourceAsMap()));
        }
        return answerDocuments;
    }

    private Map<String, Object> convertQuestionDocToMap(QuestionDocument questionDocument) {
        return objectMapper.convertValue(questionDocument, Map.class);
    }

    private QuestionDocument convertMapToQuestionDoc(Map<String, Object> map) {
        return objectMapper.convertValue(map, QuestionDocument.class);
    }

    private AnswerDocument convertMapToAnswerDoc(Map<String, Object> map) {
        return objectMapper.convertValue(map, AnswerDocument.class);
    }
}
