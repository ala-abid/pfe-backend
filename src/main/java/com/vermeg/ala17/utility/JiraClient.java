package com.vermeg.ala17.utility;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraClient {

    private String username;
    private String password;
    private String jiraUrl;
    private JiraRestClient restClient;

    private JiraClient(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
        this.restClient = getJiraRestClient();
    }

    public static void main(String[] args) throws IOException {

        JiraClient jiraClient = new JiraClient("abidala99", "aZerty123!", "https://open-jira.nrao.edu");

//        final String issueKey = jiraClient.createIssue("ABCD", 1L, "Issue created from JRJC");
//        jiraClient.updateIssueDescription(issueKey, "This is description from my Jira Client");
//        Issue issue = jiraClient.getIssue(issueKey);
//        System.out.println(issue.getDescription());
//
//        jiraClient.voteForAnIssue(issue);
//
//        System.out.println(jiraClient.getTotalVotesCount(issueKey));
//
//        jiraClient.addComment(issue, "This is comment from my Jira Client");
//
//        List<Comment> comments = jiraClient.getAllComments(issueKey);
//        comments.forEach(c -> System.out.println(c.getBody()));
//
//        jiraClient.deleteIssue(issueKey, true);
        System.out.println(jiraClient.getAllProjects());
        System.out.println("<>>>>><<<<<<>>>>"+jiraClient.searchJql());


        jiraClient.restClient.close();
    }

    private String createIssue(String projectKey, Long issueType, String issueSummary) {

        IssueRestClient issueClient = restClient.getIssueClient();

        IssueInput newIssue = new IssueInputBuilder(projectKey, issueType, issueSummary).build();

        return issueClient.createIssue(newIssue).claim().getKey();
    }

    private Issue getIssue(String issueKey) {
        return restClient.getIssueClient().getIssue(issueKey).claim();
    }

    private void voteForAnIssue(Issue issue) {
        restClient.getIssueClient().vote(issue.getVotesUri()).claim();
    }

    private int getTotalVotesCount(String issueKey) {
        BasicVotes votes = getIssue(issueKey).getVotes();
        return votes == null ? 0 : votes.getVotes();
    }

    private void addComment(Issue issue, String commentBody) {
        restClient.getIssueClient().addComment(issue.getCommentsUri(), Comment.valueOf(commentBody));
    }

    private List<Comment> getAllComments(String issueKey) {
        return StreamSupport.stream(getIssue(issueKey).getComments().spliterator(), false)
                .collect(Collectors.toList());
    }

    private void updateIssueDescription(String issueKey, String newDescription) {
        IssueInput input = new IssueInputBuilder().setDescription(newDescription).build();
        restClient.getIssueClient().updateIssue(issueKey, input).claim();
    }

    private void deleteIssue(String issueKey, boolean deleteSubtasks) {
        restClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
    }

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraUri(), this.username, this.password);
    }

    private URI getJiraUri() {
        return URI.create(this.jiraUrl);
    }

    private Iterable<BasicProject> getAllProjects(){
        return restClient.getProjectClient().getAllProjects().claim();
    }
    private Iterable<Issue> searchJql(){
        //return restClient.getSearchClient().searchJql("project = 'CASA' AND assignee = 'Juergen Ott'").claim().getIssues();
        return restClient.getSearchClient().searchJql("summary ~ 'color postscript'").claim().getIssues();
    }
}