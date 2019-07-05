package com.skywilling.cn.common.exception;

public class NoAvailableActionFoundException extends Throwable {


    private static final long serialVersionUID = 6456575038191313218L;

    public NoAvailableActionFoundException() {
        super();
    }

    public NoAvailableActionFoundException(String message) {
        super(message);
    }
}
