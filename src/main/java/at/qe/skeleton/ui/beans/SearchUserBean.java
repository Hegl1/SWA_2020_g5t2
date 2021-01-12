package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.User;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;

@Component
@Scope("view")
public class SearchUserBean implements Serializable {
    private Collection<User> results;

    private String search = null;
    private String filterRole = null;

    public Collection<User> getResults() {
        return results;
    }

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        this.results = userService.getAllUsersForAuthority();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search == null ? null : search.trim().toLowerCase();

        if(this.search == ""){
            this.search = null;
        }
    }

    public String getFilterRole() {
        return filterRole;
    }

    public void setFilterRole(final String filterRole) {
        this.filterRole = filterRole == null ? null : filterRole.trim().toUpperCase();

        if(this.filterRole == ""){
            this.filterRole = null;
        }
    }

    public void doFilter(){
        Collection<User> results = this.results = userService.getAllUsersForAuthority();

        if(search != null){
            results = userService.filterUserByUsername(results, search);
        }

        if(filterRole != null){
            results = userService.filterUserByRole(results, filterRole);
        }

        this.results = results;
    }

    public void doResetFilter(){
        this.search = null;
        this.filterRole = null;

        this.init();
    }
}