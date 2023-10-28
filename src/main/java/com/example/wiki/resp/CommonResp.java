package com.example.wiki.resp;

/*
    封装一个通用的返回类，包括业务上的成功或失败，返回信息和数据（success、message、content）
    这样controller层返回的就都是CommonResp类，CommonResp.content才是业务数据
 */
public class CommonResp<T> {
//    默认返回成功
    private boolean success = true;
//    返回信息描述，成功提示成功，失败提示失败信息
    private String message;
//    返回内容,一般是实体类，所以定义一个泛型数据
    private T content;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommonResp{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }
}
