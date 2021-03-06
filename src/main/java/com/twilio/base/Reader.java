package com.twilio.base;

import com.google.common.util.concurrent.ListenableFuture;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;

import java.util.concurrent.Callable;

/**
 * Executor for listing of a resource.
 *
 * @param <T> type of the resource
 */
public abstract class Reader<T extends Resource> {

    private static final int MAX_PAGE_SIZE = 1000;

    private Integer pageSize;
    private Long limit;

    /**
     * Execute a request using default client.
     *
     * @return ResourceSet of objects
     */
    public ResourceSet<T> read() {
        return read(Twilio.getRestClient());
    }

    /**
     * Execute a request using specified client.
     *
     * @param client client used to make request
     * @return ResourceSet of objects
     */
    public abstract ResourceSet<T> read(final TwilioRestClient client);

    /**
     * Execute an async request using default client.
     *
     * @return future that resolves to the ResourceSet of objects
     */
    public ListenableFuture<ResourceSet<T>> readAsync() {
        return readAsync(Twilio.getRestClient());
    }

    /**
     * Execute an async request using specified client.
     *
     * @param client client used to make request
     * @return future that resolves to the ResourceSet of objects
     */
    public ListenableFuture<ResourceSet<T>> readAsync(final TwilioRestClient client) {
        return Twilio.getExecutorService().submit(new Callable<ResourceSet<T>>() {
            public ResourceSet<T> call() {
                return read(client);
            }
        });
    }

    /**
     * Fetch the first page of resources.
     *
     * @return Page containing the first pageSize of resources
     */
    public Page<T> firstPage() {
        return firstPage(Twilio.getRestClient());
    }

    /**
     * Fetch the first page of resources using specified client.
     *
     * @param client client used to fetch
     * @return Page containing the first pageSize of resources
     */
    public abstract Page<T> firstPage(final TwilioRestClient client);

    /**
     * Fetch the following page of resources.
     *
     * @param page current page of resources
     * @return Page containing the first pageSize of resources
     */
    public Page<T> nextPage(final Page<T> page) {
        return nextPage(page, Twilio.getRestClient());
    }

    /**
     * Fetch the following page of resources using specified client.
     *
     * @param page current page of resources
     * @param client client used to fetch
     * @return Page containing the first pageSize of resources
     */
    public abstract Page<T> nextPage(final Page<T> page, final TwilioRestClient client);

    public Integer getPageSize() {
        return pageSize;
    }

    public Reader<T> pageSize(final int pageSize) {
        this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        return this;
    }

    public Long getLimit() {
        return limit;
    }

    /**
     * Sets the max number of records to read.
     *
     * @param limit max number of records to read
     * @return this reader
     */
    public Reader<T> limit(final long limit) {
        this.limit = limit;

        if (this.pageSize == null) {
            this.pageSize = (int)Math.min(this.limit, MAX_PAGE_SIZE);
        }

        return this;
    }

}
