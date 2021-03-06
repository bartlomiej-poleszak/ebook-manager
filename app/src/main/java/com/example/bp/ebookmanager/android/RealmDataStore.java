package com.example.bp.ebookmanager.android;

import android.content.Context;

import com.example.bp.ebookmanager.AndroidDataStore;
import com.example.bp.ebookmanager.model.Book;
import com.example.bp.ebookmanager.realm.RealmBook;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Ebook Manager
 * Created by Bartek on 2017-06-03.
 */

public class RealmDataStore implements AndroidDataStore {

    private String databaseFileName;

    public RealmDataStore(String databaseFileName) {
        this.databaseFileName = databaseFileName;
    }

    @Override
    public void initialize(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("ebookmanager.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void storeThumbnail(Book book, byte[] thumbnail) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmBook realmBook = realm.where(RealmBook.class)
                .equalTo("id", book.getId())
                .findFirst();
        realmBook.setThumbnail(thumbnail);
        realm.commitTransaction();
    }

    @Override
    public void update(Book book) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmBook realmBook = realm.where(RealmBook.class)
                .equalTo("id", book.getId())
                .findFirst();
        realmBook.fillDetails(book);
        realm.commitTransaction();
    }

    @Override
    public void update(List<Book> data) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (Book book : data)
            addBookToRealmIfNeeded(realm, book);
        realm.commitTransaction();
    }

    private void addBookToRealmIfNeeded(Realm realm, Book book) {
        RealmBook realmBook = realm.where(RealmBook.class)
                .equalTo("id", book.getId())
                .findFirst();
        if (realmBook == null) {
            realmBook = realm.createObject(RealmBook.class, book.getId());
            realmBook.fromBook(book);
        }
    }
}
