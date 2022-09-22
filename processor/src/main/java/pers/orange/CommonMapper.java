package pers.orange;

import org.mapstruct.MappingTarget;

/**
 * @author orange add
 */
public interface CommonMapper<S,T> {

    T to(S source);

    void to(S source, @MappingTarget T target);
}
