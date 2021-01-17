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

    private String filterUsername = null;
    private String filterEmail = null;
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

    public String getFilterUsername() {
        return filterUsername;
    }

    public void setFilterUsername(final String filterUsername) {
        this.filterUsername = filterUsername == null ? null : filterUsername.trim().toLowerCase();

        if(this.filterUsername != null && this.filterUsername.equals("")){
            this.filterUsername = null;
        }
    }

    public String getFilterEmail() {
        return filterEmail;
    }

    public void setFilterEmail(final String filterEmail) {
        this.filterEmail = filterEmail == null ? null : filterEmail.trim().toLowerCase();

        if(this.filterEmail != null && this.filterEmail.equals("")){
            this.filterEmail = null;
        }
    }

    public String getFilterRole() {
        return filterRole;
    }

    public void setFilterRole(final String filterRole) {
        this.filterRole = filterRole == null ? null : filterRole.trim().toUpperCase();

        if(this.filterRole != null && this.filterRole.equals("")){
            this.filterRole = null;
        }
    }

    public void doFilter(){
        Collection<User> results = this.results = userService.getAllUsersForAuthority();

        if(filterUsername != null){
            results = userService.filterUserByUsername(results, filterUsername);
        }

        if(filterEmail != null){
            results = userService.filterUserByEmail(results, filterEmail);
        }

        if(filterRole != null){
            results = userService.filterUserByRole(results, filterRole);
        }

        this.results = results;
    }

    public void doResetFilter(){
        this.filterUsername = null;
        this.filterEmail = null;
        this.filterRole = null;

        this.init();
    }
}