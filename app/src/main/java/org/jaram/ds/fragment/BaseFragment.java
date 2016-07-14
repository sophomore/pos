package org.jaram.ds.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jaram.ds.activities.BaseActivity;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import rx.Subscription;

public abstract class BaseFragment extends Fragment {

    protected abstract @LayoutRes int getLayoutResource();

    protected abstract void setupLayout(View view);

    @Nullable private BaseActivity activity;

    private Set<Subscription> subscriptionSet = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.bind(this, view);
        setupLayout(view);
        view.setClickable(true);

        if (getActivity() instanceof BaseActivity) {
            activity = (BaseActivity) getActivity();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        for (Subscription subscription : subscriptionSet) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
        super.onDestroyView();
    }

    protected void showProgress() {
        if (activity != null) {
            activity.showProgress();
        }
    }

    protected void hideProgress() {
        if (activity != null) {
            activity.hideProgress();
        }
    }

    protected void setProgressMessage(CharSequence message) {
        if (activity != null) {
            activity.setProgressMessage(message);
        }
    }

    protected Subscription addSubscription(Subscription subscription) {
        subscriptionSet.add(subscription);
        return subscription;
    }
}
