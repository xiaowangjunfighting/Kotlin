package com.example.kotlin2020;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Man {
    @Nullable
    String name;
    @NotNull
    String address;

    String email;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @NotNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NotNull String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
