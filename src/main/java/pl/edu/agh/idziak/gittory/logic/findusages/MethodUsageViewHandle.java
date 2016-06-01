package pl.edu.agh.idziak.gittory.logic.findusages;

import javafx.util.Callback;

/**
 * Created by Tomasz on 28.05.2016.
 */
public class MethodUsageViewHandle {
    private Callback<MethodUsage, Void> doubleClickCallback;

    private MethodUsageViewHandle(Builder builder) {
        doubleClickCallback = builder.doubleClickCallback;
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public void doubleClick(MethodUsage methodUsage) {
        if (doubleClickCallback == null)
            return;
        doubleClickCallback.call(methodUsage);
    }


    public static final class Builder {
        private Callback<MethodUsage, Void> doubleClickCallback;

        private Builder() {
        }

        public Builder doubleClickCallback(Callback<MethodUsage, Void> val) {
            doubleClickCallback = val;
            return this;
        }

        public MethodUsageViewHandle build() {
            return new MethodUsageViewHandle(this);
        }
    }
}
