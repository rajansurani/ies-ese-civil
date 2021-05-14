package com.upscrks.iesesecivil.Utils;

import java.util.List;

import androidx.annotation.Nullable;

public interface OnCompleteListener<T> {
    void OnComplete(@Nullable List<T> list);
}
