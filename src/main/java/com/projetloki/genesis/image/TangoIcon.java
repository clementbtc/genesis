package com.projetloki.genesis.image;

import java.io.IOException;

import com.google.common.base.Preconditions;

/**
 * A catalog of icons from the Tango Desktop Project. Every icon is available in
 * three sizes - 16x16, 22x22 and 32x32.
 *
 * <p>Example: {@code TangoIcon.APPLICATIONS_GAMES.toImage(16)}</p>
 *
 * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
 * @author Clément Roux
 */
public enum TangoIcon {

  /**
   * <img style="background-color:white" src="TangoIcon/ACCESSORIES_CALCULATOR.png"/>
   */
  ACCESSORIES_CALCULATOR("apps/accessories-calculator.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/ACCESSORIES_CHARACTER_MAP.png"/>
   */
  ACCESSORIES_CHARACTER_MAP("apps/accessories-character-map.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/ACCESSORIES_TEXT_EDITOR.png"/>
   */
  ACCESSORIES_TEXT_EDITOR("apps/accessories-text-editor.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/ADDRESS_BOOK_NEW.png"/>
   */
  ADDRESS_BOOK_NEW("actions/address-book-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_ACCESSORIES.png"/>
   */
  APPLICATIONS_ACCESSORIES("categories/applications-accessories.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_DEVELOPMENT.png"/>
   */
  APPLICATIONS_DEVELOPMENT("categories/applications-development.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_GAMES.png"/>
   */
  APPLICATIONS_GAMES("categories/applications-games.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_GRAPHICS.png"/>
   */
  APPLICATIONS_GRAPHICS("categories/applications-graphics.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_INTERNET.png"/>
   */
  APPLICATIONS_INTERNET("categories/applications-internet.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_MULTIMEDIA.png"/>
   */
  APPLICATIONS_MULTIMEDIA("categories/applications-multimedia.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_OFFICE.png"/>
   */
  APPLICATIONS_OFFICE("categories/applications-office.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_OTHER.png"/>
   */
  APPLICATIONS_OTHER("categories/applications-other.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATIONS_SYSTEM.png"/>
   */
  APPLICATIONS_SYSTEM("categories/applications-system.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATION_CERTIFICATE.png"/>
   */
  APPLICATION_CERTIFICATE("mimetypes/application-certificate.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPLICATION_X_EXECUTABLE.png"/>
   */
  APPLICATION_X_EXECUTABLE("mimetypes/application-x-executable.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/APPOINTMENT_NEW.png"/>
   */
  APPOINTMENT_NEW("actions/appointment-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_CARD.png"/>
   */
  AUDIO_CARD("devices/audio-card.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_INPUT_MICROPHONE.png"/>
   */
  AUDIO_INPUT_MICROPHONE("devices/audio-input-microphone.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_VOLUME_HIGH.png"/>
   */
  AUDIO_VOLUME_HIGH("status/audio-volume-high.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_VOLUME_LOW.png"/>
   */
  AUDIO_VOLUME_LOW("status/audio-volume-low.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_VOLUME_MEDIUM.png"/>
   */
  AUDIO_VOLUME_MEDIUM("status/audio-volume-medium.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_VOLUME_MUTED.png"/>
   */
  AUDIO_VOLUME_MUTED("status/audio-volume-muted.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/AUDIO_X_GENERIC.png"/>
   */
  AUDIO_X_GENERIC("mimetypes/audio-x-generic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/BATTERY.png"/>
   */
  BATTERY("devices/battery.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/BATTERY_CAUTION.png"/>
   */
  BATTERY_CAUTION("status/battery-caution.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/BOOKMARK_NEW.png"/>
   */
  BOOKMARK_NEW("actions/bookmark-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/CAMERA_PHOTO.png"/>
   */
  CAMERA_PHOTO("devices/camera-photo.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/CAMERA_VIDEO.png"/>
   */
  CAMERA_VIDEO("devices/camera-video.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/COMPUTER.png"/>
   */
  COMPUTER("devices/computer.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/CONTACT_NEW.png"/>
   */
  CONTACT_NEW("actions/contact-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DIALOG_ERROR.png"/>
   */
  DIALOG_ERROR("status/dialog-error.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DIALOG_INFORMATION.png"/>
   */
  DIALOG_INFORMATION("status/dialog-information.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DIALOG_WARNING.png"/>
   */
  DIALOG_WARNING("status/dialog-warning.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_NEW.png"/>
   */
  DOCUMENT_NEW("actions/document-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_OPEN.png"/>
   */
  DOCUMENT_OPEN("actions/document-open.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_PRINT.png"/>
   */
  DOCUMENT_PRINT("actions/document-print.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_PRINT_PREVIEW.png"/>
   */
  DOCUMENT_PRINT_PREVIEW("actions/document-print-preview.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_PROPERTIES.png"/>
   */
  DOCUMENT_PROPERTIES("actions/document-properties.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_SAVE.png"/>
   */
  DOCUMENT_SAVE("actions/document-save.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DOCUMENT_SAVE_AS.png"/>
   */
  DOCUMENT_SAVE_AS("actions/document-save-as.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DRIVE_HARDDISK.png"/>
   */
  DRIVE_HARDDISK("devices/drive-harddisk.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DRIVE_OPTICAL.png"/>
   */
  DRIVE_OPTICAL("devices/drive-optical.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/DRIVE_REMOVABLE_MEDIA.png"/>
   */
  DRIVE_REMOVABLE_MEDIA("devices/drive-removable-media.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_CLEAR.png"/>
   */
  EDIT_CLEAR("actions/edit-clear.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_COPY.png"/>
   */
  EDIT_COPY("actions/edit-copy.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_CUT.png"/>
   */
  EDIT_CUT("actions/edit-cut.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_DELETE.png"/>
   */
  EDIT_DELETE("actions/edit-delete.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_FIND.png"/>
   */
  EDIT_FIND("actions/edit-find.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_FIND_REPLACE.png"/>
   */
  EDIT_FIND_REPLACE("actions/edit-find-replace.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_PASTE.png"/>
   */
  EDIT_PASTE("actions/edit-paste.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_REDO.png"/>
   */
  EDIT_REDO("actions/edit-redo.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_SELECT_ALL.png"/>
   */
  EDIT_SELECT_ALL("actions/edit-select-all.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EDIT_UNDO.png"/>
   */
  EDIT_UNDO("actions/edit-undo.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_FAVORITE.png"/>
   */
  EMBLEM_FAVORITE("emblems/emblem-favorite.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_IMPORTANT.png"/>
   */
  EMBLEM_IMPORTANT("emblems/emblem-important.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_PHOTOS.png"/>
   */
  EMBLEM_PHOTOS("emblems/emblem-photos.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_READONLY.png"/>
   */
  EMBLEM_READONLY("emblems/emblem-readonly.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_SYMBOLIC_LINK.png"/>
   */
  EMBLEM_SYMBOLIC_LINK("emblems/emblem-symbolic-link.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_SYSTEM.png"/>
   */
  EMBLEM_SYSTEM("emblems/emblem-system.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/EMBLEM_UNREADABLE.png"/>
   */
  EMBLEM_UNREADABLE("emblems/emblem-unreadable.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_ANGEL.png"/>
   */
  FACE_ANGEL("emotes/face-angel.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_CRYING.png"/>
   */
  FACE_CRYING("emotes/face-crying.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_DEVILISH.png"/>
   */
  FACE_DEVILISH("emotes/face-devilish.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_GLASSES.png"/>
   */
  FACE_GLASSES("emotes/face-glasses.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_GRIN.png"/>
   */
  FACE_GRIN("emotes/face-grin.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_KISS.png"/>
   */
  FACE_KISS("emotes/face-kiss.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_MONKEY.png"/>
   */
  FACE_MONKEY("emotes/face-monkey.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_PLAIN.png"/>
   */
  FACE_PLAIN("emotes/face-plain.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_SAD.png"/>
   */
  FACE_SAD("emotes/face-sad.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_SMILE.png"/>
   */
  FACE_SMILE("emotes/face-smile.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_SMILE_BIG.png"/>
   */
  FACE_SMILE_BIG("emotes/face-smile-big.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_SURPRISE.png"/>
   */
  FACE_SURPRISE("emotes/face-surprise.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FACE_WINK.png"/>
   */
  FACE_WINK("emotes/face-wink.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER.png"/>
   */
  FOLDER("places/folder.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER_DRAG_ACCEPT.png"/>
   */
  FOLDER_DRAG_ACCEPT("status/folder-drag-accept.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER_NEW.png"/>
   */
  FOLDER_NEW("actions/folder-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER_OPEN.png"/>
   */
  FOLDER_OPEN("status/folder-open.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER_REMOTE.png"/>
   */
  FOLDER_REMOTE("places/folder-remote.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER_SAVED_SEARCH.png"/>
   */
  FOLDER_SAVED_SEARCH("places/folder-saved-search.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FOLDER_VISITING.png"/>
   */
  FOLDER_VISITING("status/folder-visiting.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FONT_X_GENERIC.png"/>
   */
  FONT_X_GENERIC("mimetypes/font-x-generic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_INDENT_LESS.png"/>
   */
  FORMAT_INDENT_LESS("actions/format-indent-less.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_INDENT_MORE.png"/>
   */
  FORMAT_INDENT_MORE("actions/format-indent-more.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_JUSTIFY_CENTER.png"/>
   */
  FORMAT_JUSTIFY_CENTER("actions/format-justify-center.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_JUSTIFY_FILL.png"/>
   */
  FORMAT_JUSTIFY_FILL("actions/format-justify-fill.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_JUSTIFY_LEFT.png"/>
   */
  FORMAT_JUSTIFY_LEFT("actions/format-justify-left.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_JUSTIFY_RIGHT.png"/>
   */
  FORMAT_JUSTIFY_RIGHT("actions/format-justify-right.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_TEXT_BOLD.png"/>
   */
  FORMAT_TEXT_BOLD("actions/format-text-bold.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_TEXT_ITALIC.png"/>
   */
  FORMAT_TEXT_ITALIC("actions/format-text-italic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_TEXT_STRIKETHROUGH.png"/>
   */
  FORMAT_TEXT_STRIKETHROUGH("actions/format-text-strikethrough.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/FORMAT_TEXT_UNDERLINE.png"/>
   */
  FORMAT_TEXT_UNDERLINE("actions/format-text-underline.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_BOTTOM.png"/>
   */
  GO_BOTTOM("actions/go-bottom.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_DOWN.png"/>
   */
  GO_DOWN("actions/go-down.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_FIRST.png"/>
   */
  GO_FIRST("actions/go-first.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_HOME.png"/>
   */
  GO_HOME("actions/go-home.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_JUMP.png"/>
   */
  GO_JUMP("actions/go-jump.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_LAST.png"/>
   */
  GO_LAST("actions/go-last.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_NEXT.png"/>
   */
  GO_NEXT("actions/go-next.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_PREVIOUS.png"/>
   */
  GO_PREVIOUS("actions/go-previous.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_TOP.png"/>
   */
  GO_TOP("actions/go-top.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/GO_UP.png"/>
   */
  GO_UP("actions/go-up.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/HELP_BROWSER.png"/>
   */
  HELP_BROWSER("apps/help-browser.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/IMAGE_LOADING.png"/>
   */
  IMAGE_LOADING("status/image-loading.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/IMAGE_MISSING.png"/>
   */
  IMAGE_MISSING("status/image-missing.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/IMAGE_X_GENERIC.png"/>
   */
  IMAGE_X_GENERIC("mimetypes/image-x-generic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INPUT_GAMING.png"/>
   */
  INPUT_GAMING("devices/input-gaming.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INPUT_KEYBOARD.png"/>
   */
  INPUT_KEYBOARD("devices/input-keyboard.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INPUT_MOUSE.png"/>
   */
  INPUT_MOUSE("devices/input-mouse.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INTERNET_GROUP_CHAT.png"/>
   */
  INTERNET_GROUP_CHAT("apps/internet-group-chat.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INTERNET_MAIL.png"/>
   */
  INTERNET_MAIL("apps/internet-mail.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INTERNET_NEWS_READER.png"/>
   */
  INTERNET_NEWS_READER("apps/internet-news-reader.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/INTERNET_WEB_BROWSER.png"/>
   */
  INTERNET_WEB_BROWSER("apps/internet-web-browser.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/LIST_ADD.png"/>
   */
  LIST_ADD("actions/list-add.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/LIST_REMOVE.png"/>
   */
  LIST_REMOVE("actions/list-remove.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_ATTACHMENT.png"/>
   */
  MAIL_ATTACHMENT("status/mail-attachment.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_FORWARD.png"/>
   */
  MAIL_FORWARD("actions/mail-forward.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_MARK_JUNK.png"/>
   */
  MAIL_MARK_JUNK("actions/mail-mark-junk.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_MARK_NOT_JUNK.png"/>
   */
  MAIL_MARK_NOT_JUNK("actions/mail-mark-not-junk.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_MESSAGE_NEW.png"/>
   */
  MAIL_MESSAGE_NEW("actions/mail-message-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_REPLY_ALL.png"/>
   */
  MAIL_REPLY_ALL("actions/mail-reply-all.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_REPLY_SENDER.png"/>
   */
  MAIL_REPLY_SENDER("actions/mail-reply-sender.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MAIL_SEND_RECEIVE.png"/>
   */
  MAIL_SEND_RECEIVE("actions/mail-send-receive.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_EJECT.png"/>
   */
  MEDIA_EJECT("actions/media-eject.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_FLASH.png"/>
   */
  MEDIA_FLASH("devices/media-flash.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_FLOPPY.png"/>
   */
  MEDIA_FLOPPY("devices/media-floppy.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_OPTICAL.png"/>
   */
  MEDIA_OPTICAL("devices/media-optical.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_PLAYBACK_PAUSE.png"/>
   */
  MEDIA_PLAYBACK_PAUSE("actions/media-playback-pause.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_PLAYBACK_START.png"/>
   */
  MEDIA_PLAYBACK_START("actions/media-playback-start.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_PLAYBACK_STOP.png"/>
   */
  MEDIA_PLAYBACK_STOP("actions/media-playback-stop.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_RECORD.png"/>
   */
  MEDIA_RECORD("actions/media-record.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_SEEK_BACKWARD.png"/>
   */
  MEDIA_SEEK_BACKWARD("actions/media-seek-backward.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_SEEK_FORWARD.png"/>
   */
  MEDIA_SEEK_FORWARD("actions/media-seek-forward.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_SKIP_BACKWARD.png"/>
   */
  MEDIA_SKIP_BACKWARD("actions/media-skip-backward.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MEDIA_SKIP_FORWARD.png"/>
   */
  MEDIA_SKIP_FORWARD("actions/media-skip-forward.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/MULTIMEDIA_PLAYER.png"/>
   */
  MULTIMEDIA_PLAYER("devices/multimedia-player.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_ERROR.png"/>
   */
  NETWORK_ERROR("status/network-error.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_IDLE.png"/>
   */
  NETWORK_IDLE("status/network-idle.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_OFFLINE.png"/>
   */
  NETWORK_OFFLINE("status/network-offline.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_RECEIVE.png"/>
   */
  NETWORK_RECEIVE("status/network-receive.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_SERVER.png"/>
   */
  NETWORK_SERVER("places/network-server.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_TRANSMIT.png"/>
   */
  NETWORK_TRANSMIT("status/network-transmit.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_TRANSMIT_RECEIVE.png"/>
   */
  NETWORK_TRANSMIT_RECEIVE("status/network-transmit-receive.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_WIRED.png"/>
   */
  NETWORK_WIRED("devices/network-wired.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_WIRELESS.png"/>
   */
  NETWORK_WIRELESS("devices/network-wireless.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_WIRELESS_ENCRYPTED.png"/>
   */
  NETWORK_WIRELESS_ENCRYPTED("status/network-wireless-encrypted.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/NETWORK_WORKGROUP.png"/>
   */
  NETWORK_WORKGROUP("places/network-workgroup.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/OFFICE_CALENDAR.png"/>
   */
  OFFICE_CALENDAR("apps/office-calendar.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PACKAGE_X_GENERIC.png"/>
   */
  PACKAGE_X_GENERIC("mimetypes/package-x-generic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP.png"/>
   */
  PREFERENCES_DESKTOP("categories/preferences-desktop.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_ACCESSIBILITY.png"/>
   */
  PREFERENCES_DESKTOP_ACCESSIBILITY("apps/preferences-desktop-accessibility.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_ASSISTIVE_TECHNOLOGY.png"/>
   */
  PREFERENCES_DESKTOP_ASSISTIVE_TECHNOLOGY("apps/preferences-desktop-assistive-technology.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_FONT.png"/>
   */
  PREFERENCES_DESKTOP_FONT("apps/preferences-desktop-font.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_KEYBOARD_SHORTCUTS.png"/>
   */
  PREFERENCES_DESKTOP_KEYBOARD_SHORTCUTS("apps/preferences-desktop-keyboard-shortcuts.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_LOCALE.png"/>
   */
  PREFERENCES_DESKTOP_LOCALE("apps/preferences-desktop-locale.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_MULTIMEDIA.png"/>
   */
  PREFERENCES_DESKTOP_MULTIMEDIA("apps/preferences-desktop-multimedia.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_PERIPHERALS.png"/>
   */
  PREFERENCES_DESKTOP_PERIPHERALS("categories/preferences-desktop-peripherals.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_REMOTE_DESKTOP.png"/>
   */
  PREFERENCES_DESKTOP_REMOTE_DESKTOP("apps/preferences-desktop-remote-desktop.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_SCREENSAVER.png"/>
   */
  PREFERENCES_DESKTOP_SCREENSAVER("apps/preferences-desktop-screensaver.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_THEME.png"/>
   */
  PREFERENCES_DESKTOP_THEME("apps/preferences-desktop-theme.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_DESKTOP_WALLPAPER.png"/>
   */
  PREFERENCES_DESKTOP_WALLPAPER("apps/preferences-desktop-wallpaper.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_SYSTEM.png"/>
   */
  PREFERENCES_SYSTEM("categories/preferences-system.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_SYSTEM_NETWORK_PROXY.png"/>
   */
  PREFERENCES_SYSTEM_NETWORK_PROXY("apps/preferences-system-network-proxy.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_SYSTEM_SESSION.png"/>
   */
  PREFERENCES_SYSTEM_SESSION("apps/preferences-system-session.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PREFERENCES_SYSTEM_WINDOWS.png"/>
   */
  PREFERENCES_SYSTEM_WINDOWS("apps/preferences-system-windows.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PRINTER.png"/>
   */
  PRINTER("devices/printer.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PRINTER_ERROR.png"/>
   */
  PRINTER_ERROR("status/printer-error.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PROCESS_STOP.png"/>
   */
  PROCESS_STOP("actions/process-stop.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/PROCESS_WORKING.png"/>
   */
  PROCESS_WORKING("animations/process-working.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SOFTWARE_UPDATE_AVAILABLE.png"/>
   */
  SOFTWARE_UPDATE_AVAILABLE("status/software-update-available.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SOFTWARE_UPDATE_URGENT.png"/>
   */
  SOFTWARE_UPDATE_URGENT("status/software-update-urgent.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/START_HERE.png"/>
   */
  START_HERE("places/start-here.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_FILE_MANAGER.png"/>
   */
  SYSTEM_FILE_MANAGER("apps/system-file-manager.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_INSTALLER.png"/>
   */
  SYSTEM_INSTALLER("apps/system-installer.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_LOCK_SCREEN.png"/>
   */
  SYSTEM_LOCK_SCREEN("actions/system-lock-screen.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_LOG_OUT.png"/>
   */
  SYSTEM_LOG_OUT("actions/system-log-out.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_SEARCH.png"/>
   */
  SYSTEM_SEARCH("actions/system-search.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_SHUTDOWN.png"/>
   */
  SYSTEM_SHUTDOWN("actions/system-shutdown.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_SOFTWARE_UPDATE.png"/>
   */
  SYSTEM_SOFTWARE_UPDATE("apps/system-software-update.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/SYSTEM_USERS.png"/>
   */
  SYSTEM_USERS("apps/system-users.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/TAB_NEW.png"/>
   */
  TAB_NEW("actions/tab-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/TEXT_HTML.png"/>
   */
  TEXT_HTML("mimetypes/text-html.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/TEXT_X_GENERIC.png"/>
   */
  TEXT_X_GENERIC("mimetypes/text-x-generic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/TEXT_X_GENERIC_TEMPLATE.png"/>
   */
  TEXT_X_GENERIC_TEMPLATE("mimetypes/text-x-generic-template.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/TEXT_X_SCRIPT.png"/>
   */
  TEXT_X_SCRIPT("mimetypes/text-x-script.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/USER_DESKTOP.png"/>
   */
  USER_DESKTOP("places/user-desktop.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/USER_HOME.png"/>
   */
  USER_HOME("places/user-home.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/USER_TRASH.png"/>
   */
  USER_TRASH("places/user-trash.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/USER_TRASH_FULL.png"/>
   */
  USER_TRASH_FULL("status/user-trash-full.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/UTILITIES_SYSTEM_MONITOR.png"/>
   */
  UTILITIES_SYSTEM_MONITOR("apps/utilities-system-monitor.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/UTILITIES_TERMINAL.png"/>
   */
  UTILITIES_TERMINAL("apps/utilities-terminal.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/VIDEO_DISPLAY.png"/>
   */
  VIDEO_DISPLAY("devices/video-display.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/VIDEO_X_GENERIC.png"/>
   */
  VIDEO_X_GENERIC("mimetypes/video-x-generic.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/VIEW_FULLSCREEN.png"/>
   */
  VIEW_FULLSCREEN("actions/view-fullscreen.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/VIEW_REFRESH.png"/>
   */
  VIEW_REFRESH("actions/view-refresh.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_CLEAR.png"/>
   */
  WEATHER_CLEAR("status/weather-clear.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_CLEAR_NIGHT.png"/>
   */
  WEATHER_CLEAR_NIGHT("status/weather-clear-night.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_FEW_CLOUDS.png"/>
   */
  WEATHER_FEW_CLOUDS("status/weather-few-clouds.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_FEW_CLOUDS_NIGHT.png"/>
   */
  WEATHER_FEW_CLOUDS_NIGHT("status/weather-few-clouds-night.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_OVERCAST.png"/>
   */
  WEATHER_OVERCAST("status/weather-overcast.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_SEVERE_ALERT.png"/>
   */
  WEATHER_SEVERE_ALERT("status/weather-severe-alert.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_SHOWERS.png"/>
   */
  WEATHER_SHOWERS("status/weather-showers.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_SHOWERS_SCATTERED.png"/>
   */
  WEATHER_SHOWERS_SCATTERED("status/weather-showers-scattered.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_SNOW.png"/>
   */
  WEATHER_SNOW("status/weather-snow.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WEATHER_STORM.png"/>
   */
  WEATHER_STORM("status/weather-storm.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/WINDOW_NEW.png"/>
   */
  WINDOW_NEW("actions/window-new.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_ADDRESS_BOOK.png"/>
   */
  X_OFFICE_ADDRESS_BOOK("mimetypes/x-office-address-book.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_CALENDAR.png"/>
   */
  X_OFFICE_CALENDAR("mimetypes/x-office-calendar.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_DOCUMENT.png"/>
   */
  X_OFFICE_DOCUMENT("mimetypes/x-office-document.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_DOCUMENT_TEMPLATE.png"/>
   */
  X_OFFICE_DOCUMENT_TEMPLATE("mimetypes/x-office-document-template.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_DRAWING.png"/>
   */
  X_OFFICE_DRAWING("mimetypes/x-office-drawing.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_DRAWING_TEMPLATE.png"/>
   */
  X_OFFICE_DRAWING_TEMPLATE("mimetypes/x-office-drawing-template.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_PRESENTATION.png"/>
   */
  X_OFFICE_PRESENTATION("mimetypes/x-office-presentation.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_PRESENTATION_TEMPLATE.png"/>
   */
  X_OFFICE_PRESENTATION_TEMPLATE("mimetypes/x-office-presentation-template.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_SPREADSHEET.png"/>
   */
  X_OFFICE_SPREADSHEET("mimetypes/x-office-spreadsheet.png"),
  /**
   * <img style="background-color:white" src="TangoIcon/X_OFFICE_SPREADSHEET_TEMPLATE.png"/>
   */
  X_OFFICE_SPREADSHEET_TEMPLATE("mimetypes/x-office-spreadsheet-template.png");
  private final String path;

  private TangoIcon(String path) {
    this.path = path;
  }

  /**
   * Returns the image for this Tango icon with the given size.
   * @param size the length of the image sides. It must be 16, 22 or 32.
   */
  public Image toImage(int size) {
    Preconditions.checkArgument(size == 16 || size == 22 || size == 32,
        "size: %s", size);
    String name = String.format("tango-icon-theme-0.8.90/%dx%d/%s",
        size, size, path);
    try {
      return Images.load(TangoIcon.class.getResource(name));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
