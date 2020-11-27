package de.samply.share.client.validator;

import java.math.BigDecimal;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.omnifaces.util.Messages;

/**
 * An implementation of a FacesValidator to check if the entered value is a positive Integer.
 */
@FacesValidator("positiveIntegerValidator")
public class PositiveIntegerValidator implements Validator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value)
      throws ValidatorException {
    try {
      if (new BigDecimal(value.toString()).signum() < 1) {
        throw new ValidatorException(
            Messages.create("Validation failed.")
                .detail("configurationTimings_parseException")
                .error().get()
        );
      }
    } catch (NumberFormatException ex) {
      throw new ValidatorException(
          Messages.create("Validation failed.")
              .detail("configurationTimings_parseException")
              .error().get()
      );
    }
  }
}
