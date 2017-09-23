package com.uides.buyanywhere.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uides.buyanywhere.Constant;
import com.uides.buyanywhere.R;
import com.uides.buyanywhere.model.UserInfo;
import com.uides.buyanywhere.network.GetUserInfoService;
import com.uides.buyanywhere.network.retrofit.Network;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TranThanhTung on 15/09/2017.
 */

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.txt_user_info)
    TextView textUserInfo;
    @BindView(R.id.btn_get_user_info)
    Button getUserInfoButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getUserInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUserInfoService getUserInfoService = Network.getInstance().createService(GetUserInfoService.class);
                UserInfo userInfo = (UserInfo) getIntent().getExtras().getSerializable(Constant.USER_INFO);
                Observable<UserInfo> getUserInfoServiceObservable =
                        getUserInfoService.getUserInfo(userInfo.getAccessToken(), userInfo.getId());
                getUserInfoServiceObservable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<UserInfo>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(UserInfo userInfo) {
                                textUserInfo.setText(userInfo.toString());
                            }
                        });

            }
        });
    }
}
