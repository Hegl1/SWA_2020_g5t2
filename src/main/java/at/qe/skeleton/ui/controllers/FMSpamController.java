package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Controller for sending faces messages
 */
@Component
@Scope("view")
public class FMSpamController implements Serializable {

    @Autowired
    SessionInfoBean sessionInfoBean;


    /**
     * send with severity "info (green)"
     *
     * @Param text the message
     */
    public void info(String text) {
        if(sessionInfoBean.isLoggedIn()){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, text, ""));
        }

    }

    /**
     * send with severity "warning (yellow)"
     *
     * @Param text the message
     */
    public void warn(String text) {
        if(sessionInfoBean.isLoggedIn()){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN, text, ""));
        }

    }

    /**
     * send with severity "fatal (red)"
     *
     * @Param text the message
     */
    public void fatal(String text) {
        if(sessionInfoBean.isLoggedIn()){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_FATAL, text, ""));
        }

    }

    /**
     * send with severity "fatal (red)"
     *
     * @Param text the message
     */
    public void error(String text) {
        if(sessionInfoBean.isLoggedIn()){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_ERROR, text, ""));
        }

    }

}
