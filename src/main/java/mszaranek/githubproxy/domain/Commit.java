package mszaranek.githubproxy.domain;

import java.util.Objects;

public class Commit {

    private String sha;

    public Commit(String sha) {
        this.sha = sha;
    }

    public Commit() {
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commit)) return false;
        Commit commit = (Commit) o;
        return Objects.equals(sha, commit.sha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha);
    }
}
