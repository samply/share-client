package de.samply.share.client.util.connector.idmanagement.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Term<AttributeT> {

  private final Consumer<AttributeT> attributeSetter;
  private final Supplier<AttributeT> attributeGetter;

  /**
   * Todo David.
   * @param attributeSetter Todo David
   * @param attributeGetter Todo David
   */
  public Term(Consumer<AttributeT> attributeSetter, Supplier<AttributeT> attributeGetter) {

    this.attributeGetter = attributeGetter;
    this.attributeSetter = attributeSetter;

  }

  public AttributeT getAttribute() {
    return attributeGetter.get();
  }

  public void setAttribute(AttributeT attributeT) {
    attributeSetter.accept(attributeT);
  }

}
