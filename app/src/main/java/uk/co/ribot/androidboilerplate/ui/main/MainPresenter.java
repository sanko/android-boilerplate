package uk.co.ribot.androidboilerplate.ui.main;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;
import uk.co.ribot.androidboilerplate.BoilerplateApplication;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.data.DataManager;
import uk.co.ribot.androidboilerplate.data.model.Ribot;
import uk.co.ribot.androidboilerplate.ui.base.BasePresenter;
import uk.co.ribot.androidboilerplate.util.SchedulerAppliers;

public class MainPresenter extends BasePresenter<MainMvpView> {

    @Inject
    protected DataManager mDataManager;
    private Subscription mSubscription;

    public MainPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
        BoilerplateApplication.get(getContext()).getComponent().inject(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadRibots() {
        checkViewAttached();
        mSubscription = mDataManager.getRibots()
                .compose(SchedulerAppliers.<List<Ribot>>defaultSchedulers(getContext()))
                .subscribe(new Subscriber<List<Ribot>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the ribots.");
                        String errorString = getContext().getString(R.string.error_loading_ribots);
                        getMvpView().showError(errorString);
                    }

                    @Override
                    public void onNext(List<Ribot> ribots) {
                        if (ribots.isEmpty()) {
                            getMvpView().showRibotsEmpty();
                        } else {
                            getMvpView().showRibots(ribots);
                        }
                    }
                });
    }

}
