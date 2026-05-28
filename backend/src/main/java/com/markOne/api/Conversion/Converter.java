package com.markOne.api.Conversion;

import com.markOne.api.Entity.FileMetaData;
import com.markOne.api.Enum.ConversionType;

import java.util.List;

public interface Converter {
    byte[] convert(List<FileMetaData> inputs);
    boolean supports(ConversionType type);
}
