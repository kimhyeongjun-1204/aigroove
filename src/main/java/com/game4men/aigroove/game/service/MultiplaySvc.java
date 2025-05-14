package com.game4men.aigroove.game.service;

import com.game4men.aigroove.common.entity.GameRoom;
import com.game4men.aigroove.common.entity.GameStatus;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.GameRoomRepository;
import com.game4men.aigroove.common.repository.GameStatusRepository;
import com.game4men.aigroove.common.repository.UserRepository;
import com.game4men.aigroove.game.DTO.GameRoomDTO;
import com.game4men.aigroove.game.DTO.PlayResultDTO;
import com.game4men.aigroove.game.DTO.PlayStatusDTO;
import com.game4men.aigroove.game.exception.DownloadIncompleteException;
import com.game4men.aigroove.game.exception.GameAlreadyStartedException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultiplaySvc {
    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;
    private final GameStatusRepository gameStatusRepository;

    private final String storageLocation = "C:\\dev\\AIGroove_api\\song_files"; // 실제 경로로 설정
    
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

            gameRoomRepository.save(gameRoom);

            return roomCode;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public GameRoomDTO getGameRoomData(String roomCode){
        GameRoom gameRoom = gameRoomRepository.findById(roomCode).get();
        
        String key = Paths.get(gameRoom.getPlayfileUri()).getFileName().toString();

        GameRoomDTO dto = new GameRoomDTO();
        dto.setGuest_id(gameRoom.getGuest() != null ? gameRoom.getGuest().getUser_id() : null);
        dto.setHas_guest(gameRoom.getHasGuest());
        dto.setHost_id(gameRoom.getHost().getUser_id());
        dto.setPlay_file_key(key);
        dto.setRoom_code(roomCode);
        dto.setIs_download_complete(gameRoom.getIsDownloadComplete());
        dto.setIs_game_started(gameRoom.getIsGameStarted());

        return dto;
    }

    public void updateGameRoomData(GameRoomDTO dto){
        GameRoom gameRoom = gameRoomRepository.findById(dto.getRoom_code()).get();
        User host = userRepository.findById(dto.getHost_id()).get();
        gameRoom.setHost(host);
        
        if (dto.getGuest_id() != null) {
            User guest = userRepository.findById(dto.getGuest_id()).get();
            gameRoom.setGuest(guest);
        } else {
            gameRoom.setGuest(null);
        }
        
        gameRoom.setHasGuest(dto.getHas_guest());
        gameRoom.setIsDownloadComplete(dto.getIs_download_complete());
        gameRoom.setIsGameStarted(dto.getIs_game_started());
        String playfileUri = getPathUriFromKey(dto.getPlay_file_key());
        gameRoom.setPlayfileUri(playfileUri);
        
        gameRoomRepository.save(gameRoom);
    }

    public byte[] getPlayFile(String key){
        try {
            Path filePath = Paths.get(storageLocation).resolve(key);
            return Files.readAllBytes(filePath);
        } catch (Exception ex) {
            throw new RuntimeException("파일을 찾을 수 없습니다", ex);
        }
    }

    public void startGame(String room_code) throws Exception{
        GameRoom gameRoom = gameRoomRepository.findById(room_code).get();

        if (gameRoom.getGuest() == null) throw new NoSuchElementException();
        if (gameRoom.getIsDownloadComplete() == false) throw new DownloadIncompleteException();
        if (gameRoom.getIsGameStarted() == true) throw new GameAlreadyStartedException();

        gameRoom.setIsGameStarted(true);
        gameRoomRepository.save(gameRoom);

        GameStatus hostGameStatus = new GameStatus();
        GameStatus guestGameStatus = new GameStatus();
        hostGameStatus.setUser(gameRoom.getHost());
        hostGameStatus.setGameRoom(gameRoom);
        guestGameStatus.setUser(gameRoom.getGuest());
        guestGameStatus.setGameRoom(gameRoom);

        gameStatusRepository.save(hostGameStatus);
        gameStatusRepository.save(guestGameStatus);
    }

    public void updatePlayStatus(User user, PlayStatusDTO dto){
        GameStatus gameStatus = gameStatusRepository.findByUser(user).get();
        
        gameStatus.setCurrentProgress(dto.getCurrentProgress());
        gameStatus.setDeaths(dto.getDeaths());
        gameStatus.setHasCleared(dto.getHasCleared());
        gameStatus.setLastCheckpoint(dto.getLastCheckpoint());
        gameStatusRepository.save(gameStatus);
    }

    public PlayStatusDTO getPlayStatus(int opponentId, String room_code){
        User opponent = userRepository.findById(opponentId).get();
        GameStatus status = gameStatusRepository.findByUser(opponent).get();

        PlayStatusDTO dto = new PlayStatusDTO();
        dto.setCurrentProgress(status.getCurrentProgress());
        dto.setDeaths(status.getDeaths());
        dto.setHasCleared(status.getHasCleared());
        dto.setLastCheckpoint(status.getLastCheckpoint());

        return dto;
    }

    public void uploadGameResult(PlayResultDTO playResult){
        
        return;
    }

    public void deleteGameRoom(String roomCode){
        GameRoom gameRoom = gameRoomRepository.findById(roomCode).get();
        gameStatusRepository.deleteAllByGameRoom(gameRoom);
        gameRoomRepository.delete(gameRoom);
        return;
    }

    private String createRandomCode(){
        return new Random().ints(48, 91)
        .filter(i -> (i <= 57 || (i >= 65 && i <= 90)))
        .limit(5)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
    }

    private String getPathUriFromKey(String key){
        // 파일 저장 경로 생성
        Path filePath = Paths.get(storageLocation).resolve(key);
        return filePath.toString();
    }
}