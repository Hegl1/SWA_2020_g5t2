package at.qe.skeleton.ui.beans;

import at.qe.skeleton.services.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class BorrowBean {

    @Autowired
    private BorrowService borrowService2;

    public String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void editAction() {
        //now the action property contains the parameter
        System.out.println(action);
        borrowService2.borrowMediaForAuthenticatedUser(action);
//        return "abc";   // redirect to media/"abc".xhtml
    }

}