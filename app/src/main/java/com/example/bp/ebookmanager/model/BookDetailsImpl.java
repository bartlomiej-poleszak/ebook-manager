package com.example.bp.ebookmanager.model;

/**
 * Ebook Manager
 * Created by bp on 12.06.16.
 */
public class BookDetailsImpl implements BookDetails {
    private Person translator;
    private Publisher publisher;
    private DetailsObserver observer;

    @Override
    public Person getTranslator() {
        return translator;
    }

    public void setTranslator(Person translator) {
        this.translator = translator;
        notifyObserver();
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
        notifyObserver();
    }

    private void notifyObserver() {
        if (observer != null)
            observer.onDetailsChanged(this);
    }

    @Override
    public void setObserver(DetailsObserver observer) {
        this.observer = observer;
    }

}