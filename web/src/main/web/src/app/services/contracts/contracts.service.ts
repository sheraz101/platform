import {Injectable} from '@angular/core';
import * as Web3 from 'web3';
import {IRequestRecord} from "../../redux/requests.models";
import {NotificationService} from "../notification/notification.service";

declare let require: any;
declare let window: any;

let tokenAbi = require('./tokenContract.json');
let fundRequestAbi = require('./fundRequestContract.json');

@Injectable()
export class ContractsService {
  private _account: string = null;
  private _web3: any;

  private _tokenContract: any;
  private _fundRequestContract: any;

  private _init: boolean = false;

  private _tokenContractAddress: string = "0x441e36bc87d343e7b2f908570823b43ac4ef6cb6";
  private _fundRequestContractAddress: string = "0x43a29f127adbc1e664c389367b0a0fceee36e764";

  constructor(private _ns: NotificationService) {
    if (!this._init) {
      this.init();
    }
  }

  private async _getTransactionOptions(): Promise<any> {
    let account = await this.getAccount();
    return Promise.resolve({
      from: account,
      gas: 300000
    });
  }

  public init(): void {
    this.checkAndInstantiateWeb3();
    this.setContracts();
    this.getAccount();
  }

  private checkAndInstantiateWeb3(): void {
    // Checking if Web3 has been injected by the browser (Mist/MetaMask)
    if (typeof window.web3 !== 'undefined') {
      // Use Mist/MetaMask's provider
      this._web3 = new Web3(window.web3.currentProvider);

      if (this._web3.version.network !== '4') {
        alert('Please connect to the Rinkeby network');
      }
    } else {
      console.warn(
        'Please use a dapp browser like mist or MetaMask plugin for chrome'
      );
    }
  };

  private setContracts(): void {
    this._tokenContract = this._web3.eth.contract(tokenAbi).at(this._tokenContractAddress);
    this._fundRequestContract = this._web3.eth.contract(fundRequestAbi).at(this._fundRequestContractAddress);
  };

  private async getAccount(): Promise<string> {
    if (this._account == null) {
      this._account = await new Promise((resolve, reject) => {
        this._web3.eth.getAccounts((err, accs) => {
          if (err != null) {
            alert('There was an error fetching your accounts.');
            return;
          }

          if (accs.length === 0) {
            alert(
              'Couldn\'t get any accounts! Make sure your Ethereum client is configured correctly.'
            );
            return;
          }
          resolve(accs[0]);
        })
      }) as string;

      this._web3.eth.defaultAccount = this._account;
    }

    return Promise.resolve(this._account);
  }

  public async getUserBalance(): Promise<number> {
    let account = await this.getAccount();

    return new Promise((resolve, reject) => {
      this._tokenContract.balanceOf.call(account, function (err, result) {
        if (err) {
          reject(err);
        }

        resolve(result);
      });
    }) as Promise<number>;
  }

  public async getUserAllowance(): Promise<string> {
    let account = await this.getAccount();

    return new Promise((resolve, reject) => {
      this._tokenContract.allowance.call(account, this._fundRequestContractAddress, function (err, result) {
        err ? reject(err) : resolve(result);
      });
    }) as Promise<string>;
  }

  public async setUserAllowance(value: number): Promise<string> {
    let currentAllowance: string = await this.getUserAllowance();
    let total = this._web3.toWei(value, 'ether');

    let tx = await new Promise((resolve, reject) => {
      this._tokenContract.approve.sendTransaction(this._fundRequestContractAddress, currentAllowance, total, this._getTransactionOptions(), function (err, tx) {
        err ? reject(err) : resolve(tx);
      })
    }) as string;

    this._ns.success("Transaction 'set allowance' sent.", this._getTransactionLink(tx));

    return Promise.resolve(total);
  }

  public async fundRequest(request: IRequestRecord, value: number): Promise<string> {
    let currentAllowance: string = await this.getUserAllowance();
    let total = this._web3.toWei(value, 'ether');

    return new Promise((resolve, reject) => {
      let batch = this._web3.createBatch();
      if (+total > +currentAllowance) {
        console.log(currentAllowance, total);
        batch.add(this._tokenContract.approve.request(this._fundRequestContractAddress, currentAllowance, total, this._getTransactionOptions(), function (err, result) {
          err ? reject(err) : console.log('approve result: ', result);
        }));
      }
      batch.add(this._fundRequestContract.fund.request(total, this._web3.fromAscii(String(request.id)), '', this._getTransactionOptions(), function (err, result) {
        if (err) {
          reject(err);
        }
        resolve(total);
      }));
      batch.execute();
    }) as Promise<string>;
  }

  public getRequestBalance(request: IRequestRecord): Promise<string> {
    return new Promise((resolve, reject) => {
      return this._fundRequestContract.balance.call(request.id, function (err, result) {
        if (err) {
          reject(err);
        } else if (result) {
          resolve(result);
        }
      });
    }) as Promise<string>;
  }

  private _getTransactionLink(tx: string): string {
    // TODO: Parametrize link
    return `<a target="_blank" href="https://rinkeby.etherscan.io/tx/${tx}">Go to transaction.</a>`;
  }
}
