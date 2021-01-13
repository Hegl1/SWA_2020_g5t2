package at.qe.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;


@Component
@Scope("request")

// a bean to spam faces messages
//@ManagedBean(name = "fms", eager = true)
//@SessionScoped
public class FmsBean {

    public String data = "9";
    public Long longtext;
    public String htmlInput = "<input type='text' size='20' />";

    public void print_info(String text) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, text,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void print_warn(String text) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, text,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getLongtext() {
        return longtext;
    }

    public void setLongtext(Long longtext) {
        this.longtext = longtext;
    }

    public String getHtmlInput() {
        return htmlInput;
    }

    public void setHtmlInput(String htmlInput) {
        this.htmlInput = htmlInput;
    }
}