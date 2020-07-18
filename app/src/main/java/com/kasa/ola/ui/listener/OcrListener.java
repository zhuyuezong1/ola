package com.kasa.ola.ui.listener;

import com.webank.mbank.ocr.net.EXBankCardResult;
import com.webank.mbank.ocr.net.EXIDCardResult;
import com.webank.mbank.ocr.net.ResultOfDriverLicense;
import com.webank.mbank.ocr.net.VehicleLicenseResult;
import com.webank.mbank.ocr.net.VehicleLicenseResultOriginal;

public interface OcrListener {
    void bankOcrResult(EXBankCardResult exBankCardResult);
    void idResult(EXIDCardResult exidCardResult);
    void driverLicenseOcrResult(ResultOfDriverLicense resultOfDriverLicense);
    void vehicleLicenseOcrResult(VehicleLicenseResultOriginal vehicleLicenseResultOriginal);
}
