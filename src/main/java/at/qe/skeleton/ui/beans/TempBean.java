package at.qe.skeleton.ui.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


//@Component
//@Scope("request")
@ManagedBean(name = "tempBean", eager = true)
@SessionScoped
public class TempBean{

    public String data = "9";
    public Long longtext;
    public String htmlInput = "<input type='text' size='20' />";


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