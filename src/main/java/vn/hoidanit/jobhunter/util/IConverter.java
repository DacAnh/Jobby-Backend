package vn.hoidanit.jobhunter.util;

public interface IConverter<T,A> {
    T toDTO(T t);
    A toEntity(T t);
}
