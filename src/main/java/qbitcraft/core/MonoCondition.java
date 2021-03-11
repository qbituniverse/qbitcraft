package qbitcraft.core;

@FunctionalInterface
public interface MonoCondition<T> {
	boolean check(T arg);
}
