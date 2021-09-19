package ntson.util.struct;

import java.util.function.Function;

public class ValueWrapper<T> {
    private T value;
    public ValueWrapper(T value) {
        this.value = value;
    }
    public T get() {
        return value;
    }
    public void set(T value) {
        this.value = value;
    }
    public void replaceBy(Function<T, T> updateFunction) {
        this.value = updateFunction.apply(this.value);
    }
}
