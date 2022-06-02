package mszaranek.githubproxy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepository {

    private String name;

    private String ownerLogin;


    private Owner owner;

    private List<Branch> branches;


    private boolean isFork;

    public GithubRepository(String name, String ownerLogin, List<Branch> branches, boolean isFork) {
        this.name = name;
        this.ownerLogin = ownerLogin;
        this.branches = branches;
        this.isFork = isFork;
    }

    public GithubRepository() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isFork() {
        return isFork;
    }

    public void setFork(boolean fork) {
        isFork = fork;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GithubRepository)) return false;
        GithubRepository that = (GithubRepository) o;
        return isFork == that.isFork && Objects.equals(name, that.name) && Objects.equals(ownerLogin, that.ownerLogin) && Objects.equals(branches, that.branches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ownerLogin, owner, branches, isFork);
    }
}
