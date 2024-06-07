package net.risesoft.api.utils;

import java.util.Objects;

public final class Optional<T> {
   private static final Optional<?> EMPTY = new Optional<>();
   private final T value;
   //我们可以看到两个构造方格都是private 私有的
   //说明 我们没办法在外面去new出来Optional对象
   private Optional() {
        this.value = null;
    }
   private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }
    //这个静态方法大致 是创建出一个包装值为空的一个对象因为没有任何参数赋值
   public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }
    //这个静态方法大致 是创建出一个包装值非空的一个对象 因为做了赋值
   public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }
    //这个静态方法大致是 如果参数value为空，则创建空对象，如果不为空，则创建有参对象
   public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }
 }