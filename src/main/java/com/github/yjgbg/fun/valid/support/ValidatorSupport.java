package com.github.yjgbg.fun.valid.support;

import com.github.yjgbg.fun.valid.core.Validator;

public interface ValidatorSupport<A> {
  Validator<A> self();
}
