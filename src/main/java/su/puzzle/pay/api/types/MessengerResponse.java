package su.puzzle.pay.api.types;

import java.util.*;

public record MessengerResponse(int id, MessengerMessage message, int type, int unread, int unread_out, List<MessengerUser> users) {}
