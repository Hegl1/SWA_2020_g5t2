package at.qe.skeleton.ui.controllers;

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


    /**
     * send with severity "info (green)"
     *
     * @Param text the message
     */
    public void info(String text) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_INFO, "hi  " + text, ""));
    }

    /**
     * send with severity "warning (yellow)"
     *
     * @Param text the message
     */
    public void warn(String text) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage("asGrowl", new FacesMessage(FacesMessage.SEVERITY_WARN, "nice to  " + text, ""));
    }

}
