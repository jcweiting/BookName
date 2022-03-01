package com.example.second_book_exchange.api;

import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.BookOuterData;
import com.example.second_book_exchange.PublicMessageData;
import com.example.second_book_exchange.UserBasicData;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RequestApi {

    //import io.reactivex  get和Observable中間不能有空格
    //Call API的地方,取得所有書籍的API (ex. for HomeFragment)
    //Manifest要加上網路協定
    //拿資料
    @POST("api/books/allList")
    Observable<ArrayList<AddBookBasicData>> getAllList(@Body UserData userDataObject);

    //放資料
    @POST("api/books/addBook")

              //後台回傳的型別                    //需要提供給後台的型別(有時候是json)
    Observable<AddBookBasicData> addBook(@Body AddBookBasicData dataObject);

    @POST("api/books/editBookData")
    Observable<AddBookBasicData> editBook(@Body AddBookBasicData dataObject);

    @POST("api/books/searchBookByBookName")
    Observable<ArrayList<AddBookBasicData>> searchBooks(@Body SearchData searchData);

    //拿個人購物車所有資料
    @POST("api/books/allCartList")
    Observable<ArrayList<AddBookBasicData>> allCartList(@Body UserData userData);

    //新增單筆購物車資料
    @POST("api/books/addCart")
    Observable<AddBookBasicData> addToCart(@Body AddBookBasicData dataObject);

    //更新單筆購物車資料
    @POST("api/books/updateCart")
    Observable<AddBookBasicData> editToCart(@Body AddBookBasicData dataObject);

    //刪除單筆購物車資料
    @POST("api/books/deleteCart")
    Observable<AddBookBasicData> deleteFromCart(@Body AddBookBasicData dataObject);

    //拿取個人最愛清單所有資料
    @POST("api/books/getAllFavorite")
    Observable<ArrayList<AddBookBasicData>> allFavoriteList(@Body UserData userData);

    //新增單筆最愛清單資料
    @POST("api/books/addFavorite")
    Observable<AddBookBasicData> addFavorite(@Body AddBookBasicData dataObject);

    //刪除單筆最愛清單資料
    @POST("api/books/deleteFavorite")
    Observable<AddBookBasicData> deleteFavorite(@Body AddBookBasicData dataObject);

    //檢查是否有使用者基本資料 or 新增個人資料 or 拿取個人資料
    @POST("api/books/checkBasicData")
    Observable<UserBasicData> checkUserData(@Body UserBasicData userDataObject);

    //編輯使用者資料
    @POST("api/books/editUserData")
    Observable<UserBasicData> editUserData(@Body UserBasicData userDataObject);

    //搜尋所有使用者資料
    @POST("api/books/searchUserAllInformation")
    Observable<UserAllInformation> searchUserAll(@Body UserBasicData userDataObject);

    //搜尋其他使用者所有資料
    @POST("api/books/searchOtherUserAllInformation")
    Observable<UserAllInformation> searchOtherUserAll(@Body UserBasicData userDataObject);

    //結帳用API
    @POST("api/books/checkOut")
    Observable<ResponseData> checkOut(@Body ArrayList<BookOuterData> bookOuterDataArr);

    //拿所有賣家資料
    @POST("api/books/getAllSellerData")
    Observable<ArrayList<UserBasicData>> getAllSellerData(@Body ArrayList<UserBasicData> userBasicDataArr);

    //拿取一本書的所有留言
    @POST("api/books/getAllMsgList")
    Observable<ArrayList<PublicMessageData>> getAllMsgList(@Body AddBookBasicData bookDataObject);

    //新增留言
    @POST("api/books/addMessage")
    Observable<ResponseData> addMessage(@Body PublicMessageData msgDataObject);

    //編輯留言
    @POST("api/books/editMessage")
    Observable<ResponseData> editMessage(@Body PublicMessageData msgDataObject);

    //取得所有訂單資料
    @POST("api/books/getAllOrderList")
    Observable<ArrayList<OrderData>> getAllOrderList(@Body UserData userDataObject);

    //SendFollow
    @POST("api/books/sendFollow")
    Observable<ResponseData> sendFollow(@Body FollowData followDataObject);

    //拿取追蹤中清單
    @POST("api/books/getFollow")
    Observable<ArrayList<FriendList>> getFollow(@Body UserData userDataObject);

    //拿取被追蹤清單
    @POST("api/books/getFollowers")
    Observable<ArrayList<FriendList>> getFollowers(@Body UserData userDataObject);

    //確認追蹤關係
    @POST("api/books/isFriend")
    Observable<ResponseData> isFriend(@Body FollowData followDataObject);

    //改變訂單狀態
    @POST("api/books/changeOrderStatus")
    Observable<ResponseData> changeOrderStatus(@Body OrderData orderDataObject);
}
