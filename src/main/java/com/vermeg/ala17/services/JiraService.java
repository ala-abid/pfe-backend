package com.vermeg.ala17.services;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.vermeg.ala17.payload.JiraIssueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JiraService {

    private JiraRestClient restClient;

    private JiraService(){
        String username = "abidala99";
        String password = "aZerty123!";
        String jiraUrl = "https://open-jira.nrao.edu";
        URI uri = URI.create(jiraUrl);
        restClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(uri, username, password);
    }

    private Iterable<Issue> searchJql(String str){
        //return restClient.getSearchClient().searchJql("project = 'CASA' AND assignee = 'Juergen Ott'").claim().getIssues();
        Set<String> setty = new HashSet<>(Arrays.asList("summary","issueType"));
        return restClient.getSearchClient().searchJql("summary ~ '"+str+"*'", 10, 0, null).claim().getIssues();
    }

    public ArrayList<String[]> getIssuesAsArray(String str){
        ArrayList<String[]> arrayList = new ArrayList<>();
        for(Issue issue: searchJql(str) ) {
            arrayList.add(new String[] {issue.getSummary(), issue.getKey()});
        }
        return arrayList;
    }

    private Iterable<BasicProject> getAllProjects(){
        return restClient.getProjectClient().getAllProjects().claim();
    }

    public JiraIssueResponse getJiraIssueResponse(String issueKey) {
        Issue issue = getIssue(issueKey);
        JiraIssueResponse jiraIssueResponse = new JiraIssueResponse();
        jiraIssueResponse.setSummary(issue.getSummary());
        jiraIssueResponse.setDescription(issue.getDescription());
        jiraIssueResponse.setAssignee(issue.getAssignee().getName());
        jiraIssueResponse.setCreationDate(issue.getCreationDate().toDate());
        jiraIssueResponse.setProjectName(issue.getProject().getName());
        List<Comment> comments =  StreamSupport.stream(issue.getComments().spliterator(), false)
                .collect(Collectors.toList());
        for(Comment comment: comments){
            jiraIssueResponse.addComment(comment.getAuthor().getName(), comment.getCreationDate().toDate(), comment.getBody());
        }
        return jiraIssueResponse;
    }

    private Issue getIssue(String issueKey) {
        return restClient.getIssueClient().getIssue(issueKey).claim();
    }

    public static void main(String[] args) {
        JiraService jiraService = new JiraService();

    }


}
