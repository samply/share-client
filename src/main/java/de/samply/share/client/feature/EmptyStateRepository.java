package de.samply.share.client.feature;

import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class EmptyStateRepository implements StateRepository {
    @Override
    public FeatureState getFeatureState(Feature feature) {
        throw new NotImplementedException();
    }

    @Override
    public void setFeatureState(FeatureState featureState) {
        throw new NotImplementedException();
    }
}
