package com.game4men.aigroove.game.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class AudioConvertSvc {
    public byte[] convertToWav(MultipartFile inputFile) throws IOException, IllegalArgumentException, InputFormatException, EncoderException {
        try{
            File sourceFile = File.createTempFile("input", null);
            File targetFile = File.createTempFile("output", ".wav");
    
            inputFile.transferTo(sourceFile);
    
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("pcm_s16le");
            audio.setBitRate(128000);
            audio.setChannels(2);
            audio.setSamplingRate(44100);
    
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);
    
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(sourceFile), targetFile, attrs);
    
            byte[] result = Files.readAllBytes(targetFile.toPath());
    
            sourceFile.delete();
            targetFile.delete();
    
            return result;
        } catch(Exception e){
            System.err.println(e);
            throw e;
        }
    }
}
