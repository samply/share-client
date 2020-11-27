package de.samply.share.client.feature;

import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

public class EmptyStateRepository implements StateRepository {

  @Override
  public FeatureState getFeatureState(Feature feature) {
    return null;
  }

  @Override
  public void setFeatureState(FeatureState featureState) {
    throw new UnsupportedOperationException();
  }
}
