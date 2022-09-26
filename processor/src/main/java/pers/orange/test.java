package pers.orange;

/**
 * @author orange add
 */
public interface test extends CommonMapper{

    @Override
    Object to(Object source);

    @Override
    void to(Object source, Object target);
}
