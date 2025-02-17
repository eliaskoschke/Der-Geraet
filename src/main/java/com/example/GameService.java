package com.example;

import org.springframework.stereotype.Service;

@Service
public class GameService {
    private boolean buttonClicked = false;

    public void buttonClicked() {
        this.buttonClicked = true;
    }

    public boolean isButtonClicked() {
        return buttonClicked;
    }
}
