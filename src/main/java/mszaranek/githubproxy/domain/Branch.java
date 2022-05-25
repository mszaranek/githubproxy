package mszaranek.githubproxy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Branch {

    private String name;
    private String sha;


    private String repositoryName;

    private Commit commit;

    public String getName() {
        return name;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Commit getCommit() {
        return commit;
    }


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
