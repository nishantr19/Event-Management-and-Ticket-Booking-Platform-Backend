package com.eventbooking.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    public String generateQRCode(String data, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        byte[] qrCodeBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    public String generateBookingQRData(Long bookingId, String bookingReference,
                                        Long eventId, String eventTitle,
                                        String userEmail, Integer numberOfSeats) {
        return String.format(
                "BOOKING_ID:%d|REF:%s|EVENT_ID:%d|EVENT:%s|USER:%s|SEATS:%d",
                bookingId, bookingReference, eventId, eventTitle, userEmail, numberOfSeats
        );
    }
}