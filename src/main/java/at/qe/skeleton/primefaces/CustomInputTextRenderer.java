package at.qe.skeleton.primefaces;

import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtext.InputTextRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class CustomInputTextRenderer extends InputTextRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {

        InputText inputText = (InputText) component;
        String clientId = inputText.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId);

        if(submittedValue != null) {

            int maxLength = inputText.getMaxlength();

            if (maxLength >= 0 && submittedValue.length() > maxLength) {
                submittedValue = submittedValue.substring(0, maxLength);
            }

            inputText.setSubmittedValue(submittedValue);
        }
    }
}
