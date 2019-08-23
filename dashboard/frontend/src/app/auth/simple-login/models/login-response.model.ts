import { User } from './user.model';
/**
 * Model of server response on user login.
 */
export class LoginResponse {
    /**
     * Token from server.
     */
    token: string;
    user: User;
}
