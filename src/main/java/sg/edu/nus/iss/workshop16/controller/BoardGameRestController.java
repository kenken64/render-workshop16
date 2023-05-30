package sg.edu.nus.iss.workshop16.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.iss.workshop16.model.Mastermind;
import sg.edu.nus.iss.workshop16.service.BoardGameService;

@RestController
@RequestMapping(path="/api/boardgame", 
            consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
public class BoardGameRestController {
    @Autowired
    private BoardGameService service;

    @PostMapping
    public ResponseEntity<String> createBoardGame(
        @RequestBody Mastermind m){
        int insertCount = service.saveGame(m);
        Mastermind result = new Mastermind();
        result.setId(m.getId());
        result.setInsertCount(insertCount);
        if(insertCount == 0){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toJSONInsert().toString());
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toJSONInsert().toString());
    }

    @GetMapping(path="{msId}")
    public ResponseEntity<String> getBoardGame(
        @PathVariable String msId
    ) throws IOException{
        Mastermind mResult  = (Mastermind) service.findById(msId);
        if(mResult == null || mResult.getName() == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("");
        }
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mResult.toJSON().toString());
    }

    @PutMapping(path="{msId}")
    public ResponseEntity<String> updateBoardGame(
        @RequestBody Mastermind m,
        @PathVariable String msId,
        @RequestParam(defaultValue = "false") boolean isUpSert
    ) throws IOException{
        Mastermind result = null;
        System.out.println("updateBoardGame");
        System.out.println("updateBoardGame " + isUpSert);
        
        if(!isUpSert){
            System.out.println("msId > " + msId);
            result = service.findById(msId);
            System.out.println("updateBoardGame result 123 " +  result);
        
            if(result == null){
                return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("bad request");
            }
        }
        if(isUpSert){
            m.setId(m.generateId(8));
        }else{
            m.setId(msId);
        }
        m.setUpSert(isUpSert);
        int updateCount = service.updateBoardGame(m);
        m.setUpdateCount(updateCount);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(m.toJSONUpdate().toString());
    }
}
