package com.game4men.aigroove.game.service;

import com.game4men.aigroove.admin.DTO.AdminResponse;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.GameRoom;
import com.game4men.aigroove.common.entity.PlayFile;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.AdminRepository;
import com.game4men.aigroove.common.repository.GameRoomRepository;
import com.game4men.aigroove.game.DTO.GameRoomDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MultiplaySvc {
    private final GameRoomRepository gameRoomRepository;

    private final String storageLocation = "파일저장경로"; // 실제 경로로 설정
    
    public String createGameRoom(User host, MultipartFile playFile) {
        try {
            Optional<GameRoom> room;
            String roomCode = "";
            boolean roomExists = true;

            for(int i = 0; i < 10; i++){
                roomCode = createRandomCode();
                if(gameRoomRepository.existsById(roomCode)) continue;
                else roomExists = false; break;
            }
            if (roomExists) throw new Exception();

            // UUID를 이용한 고유 키 생성
            String uniqueKey = UUID.randomUUID().toString();

            // 파일 저장 경로 생성
            Path filePath = Paths.get(storageLocation).resolve(uniqueKey);

            // 파일 저장
            Files.copy(playFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            GameRoom gameRoom = new GameRoom();
            gameRoom.setRoomCode(roomCode);
            gameRoom.setHost(host);
            gameRoom.setHasGuest(false);
            gameRoom.setIsDownloadComplete(false);
            gameRoom.setIsGameStarted(false);
            gameRoom.setPlayfileUri(filePath.toString());
            gameRoom.setRoomCode(roomCode);

            return roomCode;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public GameRoomDTO getGameRoomData(String roomCode){
        GameRoom gameRoom = gameRoomRepository.findById(roomCode).get();
        
        String key = Paths.get(gameRoom.getPlayfileUri()).getFileName().toString();

        GameRoomDTO dto = new GameRoomDTO();
        dto.setGuest_id(gameRoom.getGuest().getUser_id());
        dto.setHas_guest(gameRoom.getHasGuest());
        dto.setHost_id(gameRoom.getHost().getUser_id());
        dto.setPlay_file_key(key);
        dto.setRoom_code(roomCode);
        dto.set_download_complete(gameRoom.getIsDownloadComplete());
        dto.set_game_started(gameRoom.getIsGameStarted());

        return dto;
    }

    public byte[] getPlayFile(String key){
        try {
            Path filePath = Paths.get(storageLocation).resolve(key);
            return Files.readAllBytes(filePath);
        } catch (Exception ex) {
            throw new RuntimeException("파일을 찾을 수 없습니다", ex);
        }
    }

    public void uploadGameResult(){

    }

    public void deleteGameRoom(String roomCode){
        gameRoomRepository.deleteById(roomCode);
        return;
    }

    private String createRandomCode(){
        return new Random().ints(48, 123)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(5)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    }
}