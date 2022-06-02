package mszaranek.githubproxy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Branch {

    private String name;
    private String sha;

    private Commit commit;

    public Branch(String name, String sha, Commit commit) {
        this.name = name;
        this.sha = sha;
        this.commit = commit;
    }

    public Branch() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Branch)) return false;
        Branch branch = (Branch) o;
        return Objects.equals(name, branch.name) && Objects.equals(sha, branch.sha) && Objects.equals(commit, branch.commit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sha, commit);
    }
}
