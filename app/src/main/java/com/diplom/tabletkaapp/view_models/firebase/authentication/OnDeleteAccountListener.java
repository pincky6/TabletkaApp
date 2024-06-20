package com.diplom.tabletkaapp.view_models.firebase.authentication;

/**
 * Интерфейс слушателя удаления аккаунта
 */
public interface OnDeleteAccountListener {
    /**
     * Метод который вызывается при успешном удалении аккаунта
     */
    public void deleteAccountComplete();
}
