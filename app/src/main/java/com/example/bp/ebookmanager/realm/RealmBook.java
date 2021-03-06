package com.example.bp.ebookmanager.realm;

import com.example.bp.ebookmanager.model.Book;
import com.example.bp.ebookmanager.model.BookDetailsImpl;
import com.example.bp.ebookmanager.model.Person;
import com.example.bp.ebookmanager.model.Publisher;
import com.example.bp.ebookmanager.model.RawThumbnail;
import com.example.bp.ebookmanager.model.formats.FormatDetails;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Ebook Manager
 * Created by bp on 24.06.16.
 */
public class RealmBook extends RealmObject {

    @PrimaryKey
    private String id;
    private String title;
    private RealmPerson author;
    private byte[] thumbnail;
    private RealmPerson translator;
    private RealmPublisher publisher;
    private RealmList<RealmFormatData> formatDetails = new RealmList<>();

    public Book toBook() {
        BookDetailsImpl bookDetails = new BookDetailsImpl();
        if (translator != null)
            bookDetails.setTranslator(translator.toPerson());
        if (publisher != null)
            bookDetails.setPublisher(publisher.toPublisher());

        Book result = new Book(bookDetails);
        result.setId(id);
        result.setTitle(title);
        result.setAuthor(author.toPerson());
        if (thumbnail != null)
            result.setThumbnail(new RawThumbnail(thumbnail));

        for (RealmFormatData formatData : formatDetails)
            bookDetails.getFormats().add(formatData.toFormatSpecificData());
        return result;
    }

    public void fromBook(Book book) {
        title = book.getTitle();

        Person author = book.getAuthor();

        this.author = RealmPerson.createOrFind(author);
        fillFormatDetails(book);

    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    private void setTranslator(Person translator) {
        this.translator = RealmPerson.createOrFind(translator);
    }

    private void setPublisher(Publisher publisher) {
        Realm realm = Realm.getDefaultInstance();
        this.publisher = realm.where(RealmPublisher.class)
                .equalTo("name", publisher.getName())
                .findFirst();
        if (this.publisher == null) {
            this.publisher = realm.createObject(RealmPublisher.class, publisher.getName());
        }
    }

    public void fillDetails(Book book) {
        Person translator = book.getTranslator();
        if (translator != null)
            setTranslator(translator);

        Publisher publisher = book.getPublisher();
        if (publisher != null)
            setPublisher(publisher);

        fillFormatDetails(book);
    }

    private void fillFormatDetails(Book book) {
        formatDetails.clear();
        for (FormatDetails formatData : book.getFormatDetailsList()) {
            Realm realm = Realm.getDefaultInstance();
            RealmFormatData realmFormatData = realm.where(RealmFormatData.class)
                    .equalTo("id", RealmFormatData.generateId(formatData.getFormatName(), book))
                    .findFirst();
            if (realmFormatData == null)
                realmFormatData = realm.createObject(RealmFormatData.class, RealmFormatData.generateId(formatData.getFormatName(), book));
            realmFormatData.fromFormatSpecificData(formatData, book);
            formatDetails.add(realmFormatData);
        }
    }
}
